package no.java.core;

import no.java.core.ldap.AbstractDirStateFactory;
import no.java.core.ldap.GroupOfUniqueNamesConstants;
import no.java.core.model.Group;

import javax.naming.directory.Attributes;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class GroupDirStateFactory extends AbstractDirStateFactory {
    public GroupDirStateFactory() {
        super(Group.class);
        addObjectClass(GroupOfUniqueNamesConstants.NAME);
    }

    // -----------------------------------------------------------------------
    // StateFactory
    // -----------------------------------------------------------------------

    protected Attributes convert(Object o, Attributes attributes) {
        Group group = (Group) o;
        attributes.put("cn", group.getName());
        putIfNotEmpty("description", group.getDescription());

        return attributes;
    }
}
