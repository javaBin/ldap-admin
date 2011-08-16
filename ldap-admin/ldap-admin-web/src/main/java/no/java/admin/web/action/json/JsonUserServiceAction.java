package no.java.admin.web.action.json;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ValidationAwareSupport;
import no.java.admin.web.services.AuthorizationService;
import no.java.core.UserService;
import no.java.core.model.User;

import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class JsonUserServiceAction extends ValidationAwareSupport {
    private String uid;
    private User user;
    private String group;
    private String password;

    private UserService userService;
    private AuthorizationService authorizationService;

    public JsonUserServiceAction(UserService userService, AuthorizationService authorizationService) {
        this.userService = userService;
        this.authorizationService = authorizationService;
    }

    public String store() throws Exception {
        if (!authorizationService.isCanEditUser(uid)) {
            return "forbidden";
        }

        User user = userService.getUser(uid);

        user.setFirstName(this.user.getFirstName());
        user.setLastName(this.user.getLastName());
        user.setMail(this.user.getMail());
        user.setMobilePhoneNumber(this.user.getMobilePhoneNumber());
        userService.saveUser(user);

        return ActionSupport.SUCCESS;
    }

    public String delete() {
        userService.deleteUser(uid);

        return ActionSupport.SUCCESS;
    }

    public String addGroup() throws Exception {
        if (!authorizationService.isCanEditUser(uid)) {
            return "forbidden";
        }

        List<String> groups = userService.findGroupsByMember(uid);
        groups.add(group);
        userService.saveGroups(uid, groups);

        return ActionSupport.SUCCESS;
    }

    public String removeGroup() throws Exception {
        if (!authorizationService.isCanEditUser(uid)) {
            return "forbidden";
        }

        List<String> groups = userService.findGroupsByMember(uid);
        groups.remove(group);
        userService.saveGroups(uid, groups);

        return ActionSupport.SUCCESS;
    }

    public String setPassword() throws Exception {
        if (!authorizationService.isCanEditUser(uid)) {
            return "forbidden";
        }

        userService.setPassword(uid, password);

        return ActionSupport.SUCCESS;
    }

    public String generatePassword() throws Exception {
        if (!authorizationService.isCanEditUser(uid)) {
            return "forbidden";
        }

        password = userService.resetPassword(uid);

        return ActionSupport.SUCCESS;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}
