package no.java.admin.web.action;

import com.opensymphony.xwork2.ActionSupport;
import no.java.admin.services.UserCreationRequest;
import no.java.admin.services.UserCreationRequestService;
import no.java.admin.services.UserCreationResult;
import no.java.core.model.User;

import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class UserRequestAdminAction extends ActionSupport {
    private String uid;
    private String firstName;
    private String lastName;
    private String mail;
    private String mobilePhoneNumber;

    private boolean edit;

    private User createdUser;

    private List<UserCreationRequest> userCreationRequests;
    private String requestId;
    private UserCreationRequest request;

    private UserCreationRequestService userCreationRequestService;

    public String list() {
        userCreationRequests = userCreationRequestService.getRequests();

        return "user-request-admin-list";
    }

    public String show() {
        request = userCreationRequestService.getRequest(requestId);

        uid = request.getUser().getUid();
        firstName = request.getUser().getFirstName();
        lastName = request.getUser().getLastName();
        mail = request.getUser().getMail();
        mobilePhoneNumber = request.getUser().getMobilePhoneNumber();

        edit = true;

        return "user-request-admin-detail";
    }

    public String approveRequest() throws Exception {
        request = userCreationRequestService.getRequest(requestId);

        request.getUser().setUid(uid);
        request.getUser().setFirstName(firstName);
        request.getUser().setLastName(lastName);
        request.getUser().setMail(mail);
        request.getUser().setMobilePhoneNumber(mobilePhoneNumber);

        userCreationRequestService.updateRequest(request);

        UserCreationResult userCreationResult = userCreationRequestService.approveRequest(requestId);

        createdUser = userCreationResult.getUser();

        return "user-request-admin-accepted";
    }

    public String rejectRequest() {
        userCreationRequestService.rejectRequest(requestId);

        return "user-request-admin-rejected";
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

    public User getCreatedUser() {
        return createdUser;
    }

    public List<UserCreationRequest> getUserCreationRequests() {
        return userCreationRequests;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public UserCreationRequest getRequest() {
        return request;
    }

    // -----------------------------------------------------------------------
    // Spring Properties
    // -----------------------------------------------------------------------

    public void setUserCreationRequestService(UserCreationRequestService userCreationRequestService) {
        this.userCreationRequestService = userCreationRequestService;
    }
}
