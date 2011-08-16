package no.java.core;

import no.java.core.model.User;
import no.java.core.ldap.AbstractDirStateFactory;

import javax.naming.directory.Attributes;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class UserDirStateFactory extends AbstractDirStateFactory {
    public UserDirStateFactory() {
        super(User.class);
        addObjectClass("inetOrgPerson");
    }

    // -----------------------------------------------------------------------
    // StateFactory
    // -----------------------------------------------------------------------

    protected Attributes convert(Object o, Attributes attributes) {
        User user = (User) o;
        attributes.put("uid", user.getUid());
        attributes.put("cn", user.getUid());
        attributes.put("givenName", user.getFirstName());
        attributes.put("sn", user.getLastName());
        putIfNotEmpty("mail", user.getMail());
        putIfNotEmpty("mobile", user.getMobilePhoneNumber());
        String password = user.getPassword();
        if (password != null) {
            attributes.put("userPassword", password);
        }

        return attributes;
    }
}
