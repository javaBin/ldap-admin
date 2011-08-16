package no.java.admin.web.events;

import no.java.core.Event;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LogoutEvent extends Event {
    private String username;

    public LogoutEvent(String username) {
        super("Logging out " + username, null);

        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
