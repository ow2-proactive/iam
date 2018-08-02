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
package org.ow2.proactive.iam.configuration;

public class IAMConfiguration {

    public static final String BACKEND = "iam.backend";

    public static final String SSL_CERTTIFICATE = "server.ssl.key-store";

    public static final String SSL_CERTTIFICATE_PASS="server.ssl.key-store-password";

    public static final String LDAP_HOST = "iam.ldap.host";

    public static final String LDAP_PORT = "iam.ldap.port";

    public static final String LDAP_ADMIN_DN = "iam.ldap.admin.dn";

    public static final String LDAP_ADMIN_PASS = "iam.ldap.admin.password";

    public static final String LDAP_DN_BASE ="iam.ldap.dn.base";

    public static final String LDAP_USERS_BASE="iam.ldap.users.base";

    public static final String LDAP_ROLES_BASE="iam.ldap.roles.base";

    public static final String LDAP_PASS_ALGORITHM="iam.ldap.password.encryption.algorithm";

    public static final String LDAP_IDENTITIES_FILE="iam.ldap.identities.file";

    private IAMConfiguration() {

    }
}
