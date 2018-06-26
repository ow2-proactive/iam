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

import org.ow2.proactive.iam.configuration.IAMConfiguration;
import org.ow2.proactive.iam.configuration.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class IAMBackend {

    private static final Logger logger = LoggerFactory.getLogger(IAMBackend.class);

    private static final String EMBEDDED_LDAP = "embeddedLDAP";

    private static String backend = PropertiesHelper.getValueAsString(IAMConfiguration.BACKEND);

    private IAMBackend(){}

    public static void start() throws Exception {

        if (backend.equals(EMBEDDED_LDAP)) {
            logger.info("IAM is configured to use an embedded LDAP backend");
            LDAPBackend.start();
        }
    }
}
