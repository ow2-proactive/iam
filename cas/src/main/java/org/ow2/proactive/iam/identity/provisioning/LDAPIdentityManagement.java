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
package org.ow2.proactive.iam.identity.provisioning;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.directory.api.ldap.model.cursor.Cursor;
import org.apache.directory.api.ldap.model.entry.*;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.server.core.api.DirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LDAPIdentityManagement implements IdentityManagement {

    private static final Logger logger = LoggerFactory.getLogger(Class.class);

    private DirectoryService directoryService;

    private String usersBase;

    private String encryptionAlgorithm;

    public LDAPIdentityManagement(DirectoryService directoryService, String usersBase,
            String passwordEncryptionAlgorithm) {
        if (directoryService.isStarted()) {
            this.directoryService = directoryService;
            this.usersBase = usersBase;
            this.encryptionAlgorithm = passwordEncryptionAlgorithm;
        } else
            throw new RuntimeException("Directory service is not started");

    }

    public boolean insert(Identity id) {
        try {

            Dn dn = new Dn("uid=" + id.getLogin() + "," + usersBase);
            Entry entry = directoryService.newEntry(dn);
            entry.add("uid", id.getLogin());
            entry.add("cn", id.getName());
            entry.add("sn", id.getName());
            entry.add("role", id.getRole());
            entry.add("userpassword", encryptPassword(encryptionAlgorithm, id.getPassword()));
            entry.add("objectClass", "top");
            entry.add("objectClass", "person");
            entry.add("objectClass", "organizationalPerson");
            entry.add("objectClass", "inetOrgPerson");
            entry.add("objectClass", "extensibleObject");
            directoryService.getAdminSession().add(entry);

            logger.info("Identity successfully inserted: " + dn);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public boolean edit(Identity id) {
        try {

            Dn dn = new Dn("uid=" + id.getLogin() + "," + usersBase);
            List<Modification> modifications = new ArrayList<>();
            //add modifications
            directoryService.getAdminSession().modify(dn, modifications);

            logger.info("Entry successfully edited: " + dn);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public boolean delete(Identity id) {
        try {

            Dn dn = new Dn("uid=" + id.getLogin() + "," + usersBase);
            directoryService.getAdminSession().delete(dn);

            logger.info("Identity successfully deleted: " + dn);
            return true;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public boolean search(Identity id) {
        try {

            Dn dn = new Dn("uid=" + id.getLogin() + "," + usersBase);
            Cursor<Entry> cursor = directoryService.getAdminSession().search(dn, "(objectclass=person)");
            //Iterator<Entry> iterator = cursor.iterator();

            for (Entry entry : cursor) {
                logger.info("Identity found: " + entry.getDn().toString());
            }

            return true;

        } catch (Exception e) {
            logger.error(e.getMessage());
            return false;
        }
    }

    public String encryptPassword(String algorithm, String _password) {
        String sEncrypted = _password;
        if ((_password != null) && (_password.length() > 0)) {
            boolean bMD5 = algorithm.equalsIgnoreCase("MD5");
            boolean bSHA = algorithm.equalsIgnoreCase("SHA") || algorithm.equalsIgnoreCase("SHA1") ||
                           algorithm.equalsIgnoreCase("SHA-1");
            if (bSHA || bMD5) {
                String sAlgorithm = "MD5";
                if (bSHA) {
                    sAlgorithm = "SHA";
                }
                try {
                    MessageDigest md = MessageDigest.getInstance(sAlgorithm);
                    md.update(_password.getBytes("UTF-8"));
                    sEncrypted = "{" + sAlgorithm + "}" + (Base64.getEncoder().encode(md.digest()));
                } catch (Exception e) {
                    sEncrypted = null;
                    logger.error(e.getMessage());
                }
            }
        }
        return sEncrypted;
    }
}
