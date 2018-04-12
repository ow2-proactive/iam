package org.ow2.proactive.iam.startup;

import org.ow2.proactive.iam.util.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IAMBackend {

    private static final Logger logger = LoggerFactory.getLogger(IAMBackend.class);
    private static final String propertiesFile = "classpath:/config/iam/iam.properties";
    private static final String embeddedLDAP = "embeddedLDAP";

    private static String backend;

    public static void start() throws Exception{
        logger.info("Loading IAM configuration");
        loadIAMProperties();

        if (backend.equals(embeddedLDAP)){
            logger.info("IAM is configured to use an embedded LDAP backend");
            LDAPBackend.start();
        }
    }

   private static void loadIAMProperties() {
       PropertiesHelper propertiesHelper = new PropertiesHelper(propertiesFile);
       backend = propertiesHelper.getValueAsString("iam.backend",null);
   }
}