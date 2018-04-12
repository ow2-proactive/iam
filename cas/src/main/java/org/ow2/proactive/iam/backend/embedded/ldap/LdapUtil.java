package org.ow2.proactive.iam.backend.embedded.ldap;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.ldif.LdifEntry;
import org.apache.directory.api.ldap.model.ldif.LdifReader;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.MutableAttributeType;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.partition.impl.avl.AvlPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;

public class LdapUtil {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private DirectoryService directoryService ;

    public LdapUtil(DirectoryService directoryService){
        if (directoryService.isStarted())
            this.directoryService = directoryService;
        else
            throw new RuntimeException("Directory service is not started");
    }

    public void addRoleAttribute() throws LdapException {

        MutableAttributeType roleAttributeType = new MutableAttributeType( "1.1.1.1.1.1" );
        roleAttributeType.setSyntaxOid( " 1.3.6.1.4.1.1466.115.121.1.44" );
        roleAttributeType.setNames( "role" );
        roleAttributeType.setUserModifiable(true);
        roleAttributeType.setEnabled( true );

        directoryService.getSchemaManager().add( roleAttributeType );
        logger.debug("Atrribute type 'role' added to LDAP schema");
    }

    public void addPartition(Dn dn) throws Exception {

        AvlPartition partition = new AvlPartition(directoryService.getSchemaManager());
        partition.setId(dn.getName());
        partition.setSuffixDn(dn);
        directoryService.addPartition(partition);
        logger.debug("Partition '"+ dn.toString() +"' added to LDAP schema");
    }

    public void importLdif(BufferedInputStream ldifStream) {

        try (LdifReader ldifReader = new LdifReader(ldifStream)) {
            for (LdifEntry ldifEntry : ldifReader) {
                //checkPartition(directoryService,ldifEntry);
                directoryService.getAdminSession().add(new DefaultEntry(directoryService.getSchemaManager(), ldifEntry.getEntry()));
            }
            logger.info("Identities added to LDAP server");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    /*private void checkPartition(DirectoryService directoryService, LdifEntry ldifEntry) throws Exception {

        Dn dn = ldifEntry.getDn();
        Dn parent = dn.getParent();

        try {
            directoryService.getAdminSession().exists(parent);
        } catch (Exception e) {

            AvlPartition partition = new AvlPartition(directoryService.getSchemaManager());
            partition.setId(dn.getName());
            partition.setSuffixDn(dn);
            directoryService.addPartition(partition);
        }
    }*/

    /*public void applyLdif(final File ldifFile) throws Exception {
        new LdifFileLoader(directoryService.getAdminSession(), ldifFile, null)
                .execute();
    }

    public void createEntry(final String id,
                            final Map<String, String[]> attributes) throws LdapException,
            LdapInvalidDnException {

        Dn dn = new Dn(directoryService.getSchemaManager(), id);
        if (!directoryService.getAdminSession().exists(dn)) {
            Entry entry = directoryService.newEntry(dn);
            for (String attributeId : attributes.keySet()) {
                entry.add(attributeId, attributes.get(attributeId));
            }
            directoryService.getAdminSession().add(entry);
        }
    }*/
}
