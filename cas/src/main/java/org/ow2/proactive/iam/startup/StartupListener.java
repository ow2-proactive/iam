package org.ow2.proactive.iam.startup;

import org.jasig.cas.client.util.CommonUtils;
import org.ow2.proactive.iam.backend.embedded.ldap.LdapBackend;
import org.ow2.proactive.iam.configuration.IAMConfiguration;
import org.ow2.proactive.iam.exceptions.IAMException;
import org.ow2.proactive.iam.utils.SSLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

public class StartupListener implements ApplicationListener<ApplicationEvent> {

    private static final Logger LOG = LoggerFactory.getLogger(StartupListener.class);

    private static final String PA_HOME_PROPERTY = "pa.scheduler.home";
    private static final String PA_HOME_PLACEHOLDER = "${pa.scheduler.home}";

    private static final String EMBEDDED_LDAP = "embeddedLDAP";

    private static boolean listenerFired = false;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof ApplicationEnvironmentPreparedEvent && !listenerFired ) {

            for (PropertySource<?> source : ((ApplicationEnvironmentPreparedEvent) event).getEnvironment().getPropertySources()) {

                if (source.getName().equals("applicationConfigurationProperties") &&
                        source instanceof EnumerablePropertySource &&
                        source.containsProperty(IAMConfiguration.SSL_CERTTIFICATE) &&
                        source.containsProperty(IAMConfiguration.SSL_CERTTIFICATE_PASS) &&
                        source.containsProperty(IAMConfiguration.BACKEND)) {

                    LOG.info("Starting ProActive IAM");

                    // Add SSL keystore to jre cacerts
                    configureSSL(source);

                    //start IAM backend (start identity backend, load identities, ..)
                    startIdentitiesBackend(source);

                    listenerFired = true;
                }

            }
        }
    }

    private static void configureSSL(PropertySource<?> source) {

        LOG.info("Adding IAM SSL certifcate to the current JVM truststore");

        try{
            String certificatePath = (String)source.getProperty(IAMConfiguration.SSL_CERTTIFICATE);
            String certificatePassword = (String)source.getProperty(IAMConfiguration.SSL_CERTTIFICATE_PASS);

            certificatePath = fillPath(certificatePath);

            SSLUtils.mergeKeyStoreWithSystem(certificatePath,certificatePassword);

        } catch (Exception e){
            throw new IAMException("IAM startup error: SSL certificate cannot be added to the JVM truststore",e);
        }
    }


    private static void startIdentitiesBackend(PropertySource<?> source){

        if (source.getProperty(IAMConfiguration.BACKEND).equals(EMBEDDED_LDAP)) {

            LOG.info("Starting identities backend");
            LOG.info("IAM is configured to use an embedded LDAP backend");

            if (source.containsProperty(IAMConfiguration.LDAP_HOST) &&
                    source.containsProperty(IAMConfiguration.LDAP_PORT) &&
                            source.containsProperty(IAMConfiguration.LDAP_DN_BASE) &&
                                source.containsProperty(IAMConfiguration.LDAP_IDENTITIES_FILE)) {

                String ldapHost = (String)source.getProperty(IAMConfiguration.LDAP_HOST);
                int ldapPort = Integer.parseInt((String)source.getProperty(IAMConfiguration.LDAP_PORT));
                String ldapBaseDn = (String)source.getProperty(IAMConfiguration.LDAP_DN_BASE);
                String ldapIdentities = (String)source.getProperty(IAMConfiguration.LDAP_IDENTITIES_FILE);

                ldapIdentities = fillPath(ldapIdentities);

                try {
                    LdapBackend.start(ldapHost,ldapPort, ldapBaseDn, ldapIdentities);
                } catch (Exception e) {
                    throw new IAMException("IAM startup error: Identities backend cannot be started", e);
                }
            } else LOG.warn("LDAP identities backend is not properly configured. LDAP backend for IAM will not be started.");
        }
    }

    private static String fillPath(String path){

        if (path.startsWith(PA_HOME_PLACEHOLDER)){
            CommonUtils.assertNotNull(System.getProperty(PA_HOME_PROPERTY),"Property " +PA_HOME_PROPERTY +" is not set");
            path = path.replace(PA_HOME_PLACEHOLDER, System.getProperty(PA_HOME_PROPERTY));
        }
        return path;
    }

}