package no.java.admin.web.action;

import com.opensymphony.xwork2.ActionSupport;
import no.java.admin.services.UidService;
import no.java.admin.services.UserCreationRequestService;
import no.java.core.model.User;

/**
 * TODO: This should ideally be a part of UserRequestAction, but I can't get xwork to validate only a single method - trygve
 *
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class StoreUserRequestAction extends ActionSupport {
    private String uid;
    private String firstName;
    private String lastName;
    private String mail;
    private String mobilePhoneNumber;
    private String message;

    private boolean requestConfirmation;
    private boolean requestStored;
    private boolean edit;

    private UserCreationRequestService userCreationRequestService;
    private UidService uidService;

    public String validateRequest() {
        System.out.println("StoreUserRequestAction.validateRequest");
        uid = uidService.generateUid(firstName, lastName);
        System.out.println("uid = " + uid);

        edit = false;
        requestConfirmation = true;

        return "user-request-detail";
    }

    public String store() {
        User user = new User();
        user.setUid(uid);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMail(mail);
        user.setMobilePhoneNumber(mobilePhoneNumber);

        userCreationRequestService.storeRequest(user);

        message = "The request has been stored and will be acted upon shortly.";
        requestStored = true;
        edit = false;

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

    public String getMessage() {
        return message;
    }

    public boolean isRequestConfirmation() {
        return requestConfirmation;
    }

    public boolean isRequestStored() {
        return requestStored;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    // -----------------------------------------------------------------------
    // Spring Properties
    // -----------------------------------------------------------------------

    public void setUserCreationRequestService(UserCreationRequestService userCreationRequestService) {
        this.userCreationRequestService = userCreationRequestService;
    }

    public void setUidService(UidService uidService) {
        this.uidService = uidService;
    }
}
