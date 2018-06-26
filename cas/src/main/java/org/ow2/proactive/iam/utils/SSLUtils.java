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
package org.ow2.proactive.iam.utils;

import com.oneandone.compositejks.CompositeX509KeyManager;
import com.oneandone.compositejks.CompositeX509TrustManager;
import com.oneandone.compositejks.KeyStoreLoader;
import com.oneandone.compositejks.SslContextUtils;
import org.ow2.proactive.iam.configuration.IAMConfiguration;
import org.ow2.proactive.iam.configuration.PropertiesHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * Utility methods for SSL context.
 */
public final class SSLUtils {

    /**
     * The key manager algorithm to use for X509. May need to modify this when using a non-Oracle JDK.
     */
    private static String x509Algorithm = PropertiesHelper.getValueAsString(IAMConfiguration.SSL_X5098ALGORITHM);
    private static String certificateFile = PropertiesHelper.getValueAsString(IAMConfiguration.SSL_CERTTIFICATE);
    private static String certificatePassword = PropertiesHelper.getValueAsString(IAMConfiguration.SSL_CERTTIFICATE_PASS);
    private static String sslContext = PropertiesHelper.getValueAsString(IAMConfiguration.SSL_CONTEXT);

    private static ApplicationContext appContext = new ClassPathXmlApplicationContext();

    private SSLUtils() {
    }

    /**
     * Configures the default SSL context to use a merged view of the system key
     * store and a custom key store.
     *
     * @throws GeneralSecurityException, IOException
     */
    public static void mergeKeyStoreWithSystem() throws GeneralSecurityException, IOException {
        InputStream keyStoreStream = appContext.getResource(certificateFile).getInputStream();
        SSLContext.setDefault(buildMergedWithSystem(KeyStoreLoader.fromStream(keyStoreStream),certificatePassword));
    }

    /**
     * Generates an SSL context that uses a merged view of the system key store
     * and a custom key store.
     *
     * @param keyStore The custom key store.
     * @return The SSL context
     * @throws GeneralSecurityException
     */
    private static SSLContext buildMergedWithSystem(KeyStore keyStore, String password) throws GeneralSecurityException {
        String defaultAlgorithm = KeyManagerFactory.getDefaultAlgorithm();

        KeyManager[] keyManagers = { new CompositeX509KeyManager(SslContextUtils.getSystemKeyManager(x509Algorithm,
                                                                                     keyStore,
                password.toCharArray()),
                SslContextUtils.getSystemKeyManager(defaultAlgorithm, null, null)) };

        TrustManager[] trustManagers = { new CompositeX509TrustManager(SslContextUtils.getSystemTrustManager(x509Algorithm, keyStore),
                SslContextUtils.getSystemTrustManager(defaultAlgorithm, null)) };

        SSLContext context = SSLContext.getInstance(sslContext);
        context.init(keyManagers, trustManagers, null);
        return context;
    }
}
