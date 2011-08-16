package no.java.admin.web.action;

import com.opensymphony.xwork2.ActionSupport;
import no.java.core.GroupService;
import no.java.core.UserService;
import no.java.core.model.Group;
import no.java.core.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class GroupAction extends ActionSupport {
    private String name;
    private String description;
    private List<User> members;
    private TreeSet<Group> groups;

    private GroupService groupService;
    private UserService userService;

    public GroupAction(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    public String list() {
        groups = new TreeSet<Group>(new GroupNameComparator());

        groups.addAll(groupService.getGroups());

        return "group-list";
    }

    public String show() throws Exception {
        Group group = groupService.getGroup(name);

        this.name = group.getName();
        this.description = group.getDescription();

        this.members = new ArrayList<User>(group.getMembers().size());
        for (String member : group.getMembers()) {
            members.add(userService.getUserByDn(member));
        }

        return "group-detail";
    }

    public String store() {
        Group group = new Group();
        group.setName(name);
        group.setDescription(description);

        groupService.saveGroup(group);

        return "stored";
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    private static class GroupNameComparator implements Comparator<Group> {
        public int compare(Group a, Group b) {
            if (a.getName() == null || b.getName() == null) {
                return 0;
            }

            return a.getName().compareToIgnoreCase(b.getName());
        }
    }

    // -----------------------------------------------------------------------
    // Properties
    // -----------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public List<User> getMembers() {
        return members;
    }

    public TreeSet<Group> getGroups() {
        return groups;
    }
}
