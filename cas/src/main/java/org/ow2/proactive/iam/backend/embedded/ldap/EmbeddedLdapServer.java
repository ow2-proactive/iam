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

import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.naming.NamingException;

import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.factory.DefaultDirectoryServiceFactory;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public enum EmbeddedLdapServer {

    //singleton instance of EmbeddedLDAPServer
    INSTANCE;

    private static final Logger logger = LoggerFactory.getLogger(EmbeddedLdapServer.class);

    private static final String INSTANCE_NAME = "ProActiveEmbeddedLDAP-"+ new Random().nextInt();

    private static final String INSTANCE_PATH = System.getProperty("java.io.tmpdir")+ File.separator+INSTANCE_NAME;

    private static DirectoryService directoryService;

    private static LdapServer ldapServer = new LdapServer();

    private static void init(String host, Integer port) {

        try {
            DefaultDirectoryServiceFactory factory = new DefaultDirectoryServiceFactory();
            factory.init(INSTANCE_NAME);

            directoryService = factory.getDirectoryService();
            directoryService.getChangeLog().setEnabled(false);
            directoryService.setShutdownHookEnabled(true);

            InstanceLayout il = new InstanceLayout(INSTANCE_PATH);
            directoryService.setInstanceLayout(il);

            ldapServer = new LdapServer();
            ldapServer.setTransports(new TcpTransport(host, port));
            ldapServer.setDirectoryService(directoryService);

        } catch (IOException e) {
            logger.error("IOException while initializing EmbeddedLdapServer", e);
        } catch (LdapException e) {
            logger.error("LdapException while initializing EmbeddedLdapServer", e);
        } catch (NamingException e) {
            logger.error("NamingException while initializing EmbeddedLdapServer", e);
        } catch (Exception e) {
            logger.error("Exception while initializing EmbeddedLdapServer", e);
        }
    }

    public void start(String host, Integer port) throws Exception {

        if (ldapServer.isStarted()) {
            logger.warn("LDAP server is already started !!");
        } else {
            init(host, port);
            directoryService.startup();
            ldapServer.start();
            logger.info("LDAP server started");
        }
    }

    public void stop() throws Exception {

        if (!ldapServer.isStarted()) {
            logger.warn("LDAP server is not running");
        } else {
            ldapServer.stop();
            directoryService.shutdown();
        }
    }

    public DirectoryService getdirectoryService() {
        return directoryService.isStarted() ? directoryService : null;
    }
}
