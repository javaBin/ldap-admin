package no.java.core;

import no.java.core.ldap.AbstractDirObjectFactory;
import no.java.core.ldap.GroupOfUniqueNamesConstants;
import no.java.core.model.Group;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class GroupDirObjectFactory extends AbstractDirObjectFactory {

    public GroupDirObjectFactory() {
        addRequiredObjectClass(GroupOfUniqueNamesConstants.NAME);
        addRequredAttribute(GroupOfUniqueNamesConstants.CN);
        addRequredAttribute(GroupOfUniqueNamesConstants.UNIQUE_MEMBER);
    }

    // -----------------------------------------------------------------------
    // AbstractDirObjectFactory Implementation
    // -----------------------------------------------------------------------

    public Object convert() throws Exception {
        Group group = new Group();

//        group.setDn(getName().toString());
        group.setName(getStringAttribute(GroupOfUniqueNamesConstants.CN));
        group.setDescription(getStringAttribute(GroupOfUniqueNamesConstants.DESCRIPTION, null));

        Attribute uniqueMember = getAttributes().get(GroupOfUniqueNamesConstants.UNIQUE_MEMBER);

        List<String> members;
        if (uniqueMember != null) {
            NamingEnumeration<?> all = uniqueMember.getAll();
            members = new ArrayList<String>();
            while (all.hasMore()) {
                members.add(all.next().toString());
            }
        } else {
            members = Collections.emptyList();
        }

        group.setMembers(members);

        return group;
    }
}
