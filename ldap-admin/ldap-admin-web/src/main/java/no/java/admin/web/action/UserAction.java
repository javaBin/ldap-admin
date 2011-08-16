package no.java.admin.web.action;

import com.opensymphony.xwork2.ActionSupport;
import no.java.admin.web.services.AuthorizationService;
import no.java.core.GroupService;
import no.java.core.UserService;
import no.java.core.model.User;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class UserAction extends ActionSupport {
    private String uid;
    private User user;
    private String generatedPassword;
    private List<String> groups;
    private List<String> availableGroups;

    private TreeSet<User> users;

    private UserService userService;
    private GroupService groupService;
    private AuthorizationService authorizationService;

    public UserAction(UserService userService, GroupService groupService, AuthorizationService authorizationService) {
        this.userService = userService;
        this.groupService = groupService;
        this.authorizationService = authorizationService;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public String list() {
        users = new TreeSet<User>(new Comparator<User>() {
            public int compare(User a, User b) {
                if (a.getLastName() == null || b.getLastName() == null) {
                    return 0;
                }

                return a.getLastName().compareToIgnoreCase(b.getLastName());
            }
        });

        users.addAll(userService.getUsers());

        return "user-list";
    }

    public String show() throws Exception {
        user = userService.getUser(uid);
        groups = userService.findGroupsByMember(uid);
        availableGroups = groupService.getAvailableGroups(uid);

        return "user-detail";
    }

    public String store() throws Exception {
        User user = userService.getUser(uid);

        user.setFirstName(this.user.getFirstName());
        user.setLastName(this.user.getLastName());
        user.setMail(this.user.getMail());
        user.setMobilePhoneNumber(this.user.getMobilePhoneNumber());
//        user.setGroups(groups);
        userService.saveUser(user);

        return "stored";
    }

    public String storeGroups() throws Exception {
        userService.saveGroups(uid, groups);

        return "stored";
    }

    public String resetPassword() throws Exception {
        generatedPassword = userService.resetPassword(uid);

        // TODO: This should ideally return "password-reset" and let struts handle what to how, but the
        // generatedPassword value is lost.

        show();

        return "user-detail";
    }

    public String delete() {
        userService.deleteUser(uid);

        return "deleted";
    }

    // -----------------------------------------------------------------------
    // Utilities
    // -----------------------------------------------------------------------

    public boolean isCanEditDetails() {
        return authorizationService.isCanEditUser(uid);
    }

    public boolean isCanResetPassword() {
        return authorizationService.isCanEditUser(uid);
    }

    public boolean isCanEditGroups() {
        return authorizationService.isLdapAdmin();
    }

    // -----------------------------------------------------------------------
    // Web Properties
    // -----------------------------------------------------------------------

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getGeneratedPassword() {
        return generatedPassword;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getAvailableGroups() {
        return availableGroups;
    }

    public TreeSet<User> getUsers() {
        return users;
    }
}
