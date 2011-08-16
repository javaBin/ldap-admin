package no.java.quality;

import no.java.core.model.User;

import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DataQualityReport {

    public static class UserReport {
        private String dn;

        private String uid;

        private Attributes attributes;

        private User user;

        private List<String> messages = new ArrayList<String>();

        public String getDn() {
            return dn;
        }

        public void setDn(String dn) {
            this.dn = dn;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public Attributes getAttributes() {
            return attributes;
        }

        public void setAttributes(Attributes attributes) {
            this.attributes = attributes;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public List<String> getMessages() {
            return messages;
        }

        public void addMessage(String message) {
            this.messages.add(message);
        }
    }

    private List<UserReport> users = new ArrayList<UserReport>();

    private List<String> technicalErrors = new ArrayList<String>();

    private Exception exception;

    public List<UserReport> getUsers() {
        return users;
    }

    public void addUser(UserReport userReport) {
        this.users.add(userReport);
    }

    public List<String> getTechnicalErrors() {
        return technicalErrors;
    }

    public void addTechnicalErrors(String error) {
        technicalErrors.add(error);
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}
