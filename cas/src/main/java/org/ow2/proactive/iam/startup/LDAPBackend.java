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

    private static void startLDAPServer() throws Exception{
        logger.info("Starting IAM embedded LDAP server");
        EmbeddedLdapServer.INSTANCE.start(host, port);
    }

    private static void addIdentities() throws Exception {
        logger.info("Loading identities");
        LdapUtil ldapUtil = new LdapUtil(EmbeddedLdapServer.INSTANCE.getdirectoryService());
        ldapUtil.addRoleAttribute();
        ldapUtil.addPartition(new Dn(baseDn));

        ApplicationContext appContext = new ClassPathXmlApplicationContext();
        BufferedInputStream bis =  new BufferedInputStream( appContext.getResource(identitiesLDIF).getInputStream());
        ldapUtil.importLdif(bis);
    }

    private static void loadLDAPProperties() {
        logger.info("Loading LDAP properties");
        PropertiesHelper propertiesHelper = new PropertiesHelper(propertiesFile);

        host = propertiesHelper.getValueAsString("ldap.host",null);
        port= propertiesHelper.getValueAsInt("ldap.port", 0);
        baseDn =  propertiesHelper.getValueAsString("dn.base",null);
        identitiesLDIF = propertiesHelper.getValueAsString("identities.ldif",null);
    }
}
