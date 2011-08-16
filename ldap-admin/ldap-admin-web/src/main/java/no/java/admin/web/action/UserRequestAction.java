package no.java.admin.web.action;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class UserRequestAction extends ActionSupport {
    private String uid;
    private String firstName;
    private String lastName;
    private String mail;
    private String mobilePhoneNumber;

    private boolean edit;

    public String showForm() {
        edit = true;
        // when coming back from the validation screen 
        uid = null;

        return "user-request-detail";
    }

    public String showBlankForm() {
        edit = true;

        uid = null;
        firstName = null;
        lastName = null;
        mail = null;
        mobilePhoneNumber = null;

        return "user-request-detail";
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMobilePhoneNumber() {
        return mobilePhoneNumber;
    }

    public void setMobilePhoneNumber(String mobilePhoneNumber) {
        this.mobilePhoneNumber = mobilePhoneNumber;
    }

    public boolean isEdit() {
        return edit;
    }
}
