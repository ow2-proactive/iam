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
package org.ow2.proactive.iam.startup;

import java.io.BufferedInputStream;

import org.apache.directory.api.ldap.model.name.Dn;
import org.ow2.proactive.iam.backend.embedded.ldap.EmbeddedLdapServer;
import org.ow2.proactive.iam.backend.embedded.ldap.LdapUtil;
import org.ow2.proactive.iam.util.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class LDAPBackend {

    private static final Logger logger = LoggerFactory.getLogger(LDAPBackend.class);

    private static final String propertiesFile = "classpath:/config/iam/iam.properties";

    private static String host;

    private static int port;

    private static String baseDn;

    private static String identitiesLDIF;

    public static void start() throws Exception {
        loadLDAPProperties();
        startLDAPServer();
        addIdentities();
    }

    private static void startLDAPServer() throws Exception {
        logger.info("Starting IAM embedded LDAP server");
        EmbeddedLdapServer.INSTANCE.start(host, port);
    }

    private static void addIdentities() throws Exception {
        logger.info("Loading identities");
        LdapUtil ldapUtil = new LdapUtil(EmbeddedLdapServer.INSTANCE.getdirectoryService());
        ldapUtil.addRoleAttribute();
        ldapUtil.addPartition(new Dn(baseDn));

        ApplicationContext appContext = new ClassPathXmlApplicationContext();
        BufferedInputStream bis = new BufferedInputStream(appContext.getResource(identitiesLDIF).getInputStream());
        ldapUtil.importLdif(bis);
    }

    private static void loadLDAPProperties() {
        logger.info("Loading LDAP properties");
        PropertiesHelper propertiesHelper = new PropertiesHelper(propertiesFile);

        host = propertiesHelper.getValueAsString("ldap.host", null);
        port = propertiesHelper.getValueAsInt("ldap.port", 0);
        baseDn = propertiesHelper.getValueAsString("dn.base", null);
        identitiesLDIF = propertiesHelper.getValueAsString("identities.ldif", null);
    }
}
