package no.java.core;

import no.java.core.model.User;
import no.java.core.ldap.AbstractDirObjectFactory;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class UserDirObjectFactory extends AbstractDirObjectFactory {

    public final static String[] REQUIRED_ATTRIBUTES = new String[]{
        "uid",
        "givenName",
        "sn",
        "mail",
    };

    public UserDirObjectFactory() {
        addRequiredObjectClass("inetOrgPerson");

        setIgnoreAttributeExeptionDuringConvertion(true);
    }

    // -----------------------------------------------------------------------
    // AbstractDirObjectFactory Implementation
    // -----------------------------------------------------------------------

    public Object convert() throws Exception {
        User user = new User();

        user.setDn(getName().toString());
        user.setUid(getStringAttribute("uid"));
        user.setFirstName(getStringAttribute("givenName"));
        user.setLastName(getStringAttribute("sn"));
        user.setMail(getStringAttribute("mail"));
        user.setMobilePhoneNumber(getStringAttribute("mobile", null));

        return user;
    }
}
