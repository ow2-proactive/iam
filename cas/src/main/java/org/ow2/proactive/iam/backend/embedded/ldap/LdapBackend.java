/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.iam.backend.embedded.ldap;

import java.io.BufferedInputStream;
import java.io.File;

import org.apache.directory.api.ldap.model.name.Dn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class LdapBackend {

    private static final Logger LOG = LoggerFactory.getLogger(LdapBackend.class);

    private LdapBackend(){}

    public static void start(String host, int port, String baseDn, String identitiesLDIF) throws Exception {
        startLDAPServer(host, port);
        addIdentities(baseDn, identitiesLDIF);
    }

    private static void startLDAPServer(String host, int port) throws Exception {
        LOG.info("Starting IAM embedded LDAP server");
        EmbeddedLdapServer.INSTANCE.start(host, port);
    }

    private static void addIdentities(String baseDn, String identitiesLDIF) throws Exception {
        LOG.info("Loading identities");
        LdapUtils ldapUtil = new LdapUtils(EmbeddedLdapServer.INSTANCE.getdirectoryService());
        ldapUtil.addRoleAttribute();
        ldapUtil.addPartition(new Dn(baseDn));
        if (identitiesLDIF.startsWith("classpath:/")){
            ApplicationContext appContext = new ClassPathXmlApplicationContext();
            BufferedInputStream bis = new BufferedInputStream(appContext.getResource(identitiesLDIF).getInputStream());
            ldapUtil.importLdif(bis);
        } else {
            ldapUtil.importLdif(new File(identitiesLDIF));
        }
    }
}
