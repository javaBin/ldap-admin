package no.java.core;

import no.java.core.ldap.GroupOfUniqueNamesConstants;
import no.java.core.ldap.LdapTemplate;
import no.java.core.model.Group;

import javax.naming.NameNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultGroupService implements GroupService {

    // -----------------------------------------------------------------------
    // GroupService Implementation
    // -----------------------------------------------------------------------

    public List<String> getAvailableGroups(String uid) {

        List<String> currentGroups = userService.findGroupsByMember(uid);

        List<Group> groups = getGroups();
        List<String> names = new ArrayList<String>(groups.size());
        for (Group group : groups) {
            String name = group.getName();

            if (!currentGroups.contains(name)) {
                names.add(name);
            }
        }
        return names;
    }

    public List<Group> getGroups() {
        return ldapTemplate.searchObject("objectClass=" + GroupOfUniqueNamesConstants.NAME);
    }

    public Group getGroup(String cn) {
        try {
            return ldapTemplate.lookupObject(ldapTemplate.makeRdn(GroupOfUniqueNamesConstants.CN, cn));
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public void saveGroup(Group group) {
        ldapTemplate.rebindObject(ldapTemplate.makeRdn(GroupOfUniqueNamesConstants.CN, group.getName()), group);
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    private LdapTemplate<Group> ldapTemplate;

    public void setLdapTemplate(LdapTemplate<Group> ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }
}
