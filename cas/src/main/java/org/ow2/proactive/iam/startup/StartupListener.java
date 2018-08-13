package org.ow2.proactive.iam.startup;

import org.jasig.cas.client.util.CommonUtils;
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

    private static String fillPath(String path){

        if (path.startsWith(PA_HOME_PLACEHOLDER)){
            CommonUtils.assertNotNull(System.getProperty(PA_HOME_PROPERTY),"Property " +PA_HOME_PROPERTY +" is not set");
            path = path.replace(PA_HOME_PLACEHOLDER, System.getProperty(PA_HOME_PROPERTY));
        }
        return path;
    }

}