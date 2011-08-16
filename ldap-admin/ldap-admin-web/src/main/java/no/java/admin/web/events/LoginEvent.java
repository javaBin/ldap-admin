package no.java.admin.web.events;

import no.java.core.Event;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LoginEvent extends Event {
    private String username;

    public LoginEvent(String username) {
        super("Logging in " + username, null);

        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
