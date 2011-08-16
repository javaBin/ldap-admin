package no.java.admin.web.action;

import com.opensymphony.xwork2.ActionSupport;
import no.java.core.UserNotFoundException;
import no.java.core.UserService;
import no.java.core.model.User;
import org.apache.commons.lang.StringUtils;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LookupAction extends ActionSupport {

    private String uid;

    private String mail;

    private boolean noUserFound;

    private UserService userService;

    public LookupAction(UserService userService) {
        this.userService = userService;
    }

    public String execute() throws Exception {
        User user;

        if (StringUtils.isNotEmpty(uid)) {
            try {
                userService.getUser(uid);
                System.out.println("uid = " + uid);
                return "found-user";
            } catch (UserNotFoundException e) {
                // continue
            }
        } else if (StringUtils.isNotEmpty(mail)) {
            System.out.println("mail = " + mail);
            user = userService.findUserByMail(mail);
            System.out.println("user = " + user);

            if(user != null) {
                uid = user.getUid();
                System.out.println("uid = " + uid);
                return "found-user";
            }
        }

        noUserFound = true;

        return "index";
    }

    // -----------------------------------------------------------------------
    // Properties
    // -----------------------------------------------------------------------

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public boolean isNoUserFound() {
        return noUserFound;
    }
}
