package no.java.core;

import no.java.core.model.User;

import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface UserService {
    List<User> getUsers();

    User getUser(String uid) throws UserNotFoundException;

    User getUserByDn(String dn) throws UserNotFoundException;

    User getUserByMail(String mail) throws UserNotFoundException;

    User findUserByMail(String mail);

    String getDnByUid(String uid) throws UserNotFoundException;

    void saveUser(User user);

    void saveGroups(String uid, List<String> groups);

    User createUser(User user);

    void deleteUser(String uid);

    String resetPassword(String uid);

    void setPassword(String uid, String password);

    List<String> findGroupsByMember(String uid);

    /**
     * A very generic search method. Searches a whole lot of fields.
     */
    List<User> search(String query);
}
