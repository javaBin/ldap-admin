package no.java.core.ldap;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AttributeException extends RuntimeException {
    public AttributeException(String message) {
        super(message);
    }

    public AttributeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
