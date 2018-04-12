package org.ow2.proactive.iam.backend.embedded.ldap;

import java.io.IOException;
import javax.naming.NamingException;

import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.factory.DefaultDirectoryServiceFactory;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.api.ldap.model.exception.LdapException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum EmbeddedLdapServer {

    //singleton instance of EmbeddedLDAPServer
    INSTANCE;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String INSTANCE_NAME = "ProActiveEmbeddedLDAP";
    private static final String INSTANCE_PATH = "/tmp/ProActiveEmbeddedLDAP";

    private static DirectoryService directoryService;
    private static LdapServer ldapServer = new LdapServer();

    private void init(String host, Integer port) {

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
            init(host,port);
            directoryService.startup();
            ldapServer.start();
            logger.info("LDAP server started");
        }
    }

    public void stop() throws Exception {

        if (!ldapServer.isStarted()) {
          logger.warn("LDAP server is not running");
        }else {
            ldapServer.stop();
            directoryService.shutdown();
        }
    }

    public DirectoryService getdirectoryService() {
        return directoryService.isStarted() ? directoryService : null;
    }
}