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

import java.util.Map;

import org.apereo.cas.CasEmbeddedContainerUtils;
import org.apereo.cas.configuration.CasConfigurationProperties;
import org.apereo.cas.web.CasWebApplicationContext;
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


@EnableDiscoveryClient
@SpringBootApplication(exclude = { HibernateJpaAutoConfiguration.class, JerseyAutoConfiguration.class,
                                   GroovyTemplateAutoConfiguration.class, JmxAutoConfiguration.class,
                                   DataSourceAutoConfiguration.class, RedisAutoConfiguration.class,
                                   MongoAutoConfiguration.class, MongoDataAutoConfiguration.class,
                                   CassandraAutoConfiguration.class,
                                   DataSourceTransactionManagerAutoConfiguration.class,
                                   MetricsDropwizardAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class })
@EnableConfigurationProperties(CasConfigurationProperties.class)
@EnableAsync
@EnableTransactionManagement(proxyTargetClass = true)
@EnableScheduling

public class IAMApplication extends SpringBootServletInitializer {

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
        new SpringApplicationBuilder(IAMApplication.class).banner(banner)
                                                          .web(true)
                                                          .properties(properties)
                                                          .logStartupInfo(false)
                                                          .contextClass(CasWebApplicationContext.class)
                                                          .run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        final Map<String, Object> properties = CasEmbeddedContainerUtils.getRuntimeProperties(Boolean.TRUE);
        return builder.sources(IAMApplication.class)
                      .properties(properties)
                      .banner(CasEmbeddedContainerUtils.getCasBannerInstance());
    }
}
