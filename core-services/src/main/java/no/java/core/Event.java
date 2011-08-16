package no.java.core;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class Event {
    private String shortMessage;

    private String longMessage;

    protected Event(String shortMessage, String longMessage) {
        this.shortMessage = shortMessage;
        this.longMessage = longMessage;
    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getLongMessage() {
        return longMessage;
    }
}
