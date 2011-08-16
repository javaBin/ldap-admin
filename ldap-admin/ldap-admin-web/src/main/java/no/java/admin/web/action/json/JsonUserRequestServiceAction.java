package no.java.admin.web.action.json;

import com.opensymphony.xwork2.ValidationAwareSupport;
import com.opensymphony.xwork2.ActionSupport;
import no.java.admin.services.UidService;
import no.java.admin.services.UserCreationRequestService;
import no.java.admin.services.UserCreationRequest;
import no.java.core.model.User;
import org.apache.struts2.interceptor.validation.SkipValidation;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class JsonUserRequestServiceAction extends ValidationAwareSupport {
    private String firstName;
    private String lastName;
    private String mail;
    private String mobilePhoneNumber;
    private String uid;
    private String requestId;

    private UidService uidService;
    private UserCreationRequestService userCreationRequestService;

    public JsonUserRequestServiceAction(UserCreationRequestService userCreationRequestService, UidService uidService) {
        this.userCreationRequestService = userCreationRequestService;
        this.uidService = uidService;
    }

    public String validate() {
        uid = uidService.generateUid(firstName, lastName);

        return ActionSupport.SUCCESS;
    }

    public String store() {
        uid = uidService.generateUid(firstName, lastName);

        User user = new User();
        user.setUid(uid);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMail(mail);
        user.setMobilePhoneNumber(mobilePhoneNumber);
        userCreationRequestService.storeRequest(user);

        return ActionSupport.SUCCESS;
    }

    @SkipValidation
    public String accept() throws Exception {
        UserCreationRequest request = userCreationRequestService.getRequest(requestId);

        request.getUser().setUid(uid);
        request.getUser().setFirstName(firstName);
        request.getUser().setLastName(lastName);
        request.getUser().setMail(mail);
        request.getUser().setMobilePhoneNumber(mobilePhoneNumber);

        userCreationRequestService.updateRequest(request);

        userCreationRequestService.approveRequest(requestId);

        return ActionSupport.SUCCESS;
    }

    @SkipValidation
    public String reject() {
        userCreationRequestService.rejectRequest(requestId);

        return ActionSupport.SUCCESS;
    }

    // -----------------------------------------------------------------------
    // Parameters
    // -----------------------------------------------------------------------

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
