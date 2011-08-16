package no.java.admin.services;

import no.java.core.model.User;

import java.io.Serializable;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
* @version $Id$
*/
public class UserCreationRequest implements Serializable {
    private User user;
    private String requestId;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
