package org.ow2.proactive.iam.startup;

import org.apereo.cas.CasEmbeddedContainerUtils;
import org.apereo.cas.web.CasWebApplicationContext;
import org.apereo.cas.configuration.CasConfigurationProperties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.actuate.autoconfigure.MetricsDropwizardAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;


@EnableDiscoveryClient
@SpringBootApplication(exclude = { HibernateJpaAutoConfiguration.class, JerseyAutoConfiguration.class,
        GroovyTemplateAutoConfiguration.class, JmxAutoConfiguration.class, DataSourceAutoConfiguration.class, RedisAutoConfiguration.class,
        MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, CassandraAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class, MetricsDropwizardAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class })
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnableAsync
@EnableTransactionManagement(proxyTargetClass = true)
@EnableScheduling

public class IAMApplication  extends  SpringBootServletInitializer {

    private static final Logger logger = LoggerFactory.getLogger(IAMApplication.class);

    /**
     * Main entry point of the IAM web application.
     * @param args the args
     */
    public static void main(final String[] args) throws Exception {

        //start IAM backend (start identity backend, load identities, ..)
        logger.info("ProActive IAM starting");
        IAMBackend.start();

        // start CAS
        final Map<String, Object> properties = CasEmbeddedContainerUtils.getRuntimeProperties(Boolean.TRUE);
        final Banner banner = CasEmbeddedContainerUtils.getCasBannerInstance();
        new SpringApplicationBuilder(IAMApplication.class).banner(banner).web(true).properties(properties).logStartupInfo(false).contextClass(CasWebApplicationContext.class).run(args);
    }


    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        final Map<String, Object> properties = CasEmbeddedContainerUtils.getRuntimeProperties(Boolean.TRUE);
        return builder
                .sources(IAMApplication.class)
                .properties(properties)
                .banner(CasEmbeddedContainerUtils.getCasBannerInstance());
    }
}