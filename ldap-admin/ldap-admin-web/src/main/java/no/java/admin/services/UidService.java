package no.java.admin.services;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface UidService {
    String generateUid(String firstName, String lastName);
}
