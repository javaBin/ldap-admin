package no.java.admin.web.services;

import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AuthorizationService {
    private static final String LDAP_ADMIN = "ldap-admin";

    public boolean isLdapAdmin() {
        return hasRoles(LDAP_ADMIN);
    }

    public boolean isCanEditUser(String uid) {
        return hasRoles(LDAP_ADMIN) || isUser(uid);
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    private boolean isUser(String uid) {
        HttpServletRequest request = ServletActionContext.getRequest();

        return request.getRemoteUser() != null && request.getRemoteUser().equals(uid);
    }

    private boolean hasRoles(String... roles) {
        HttpServletRequest request = ServletActionContext.getRequest();

        if (request.getRemoteUser() == null) {
            return false;
        }

        for (String role : roles) {
            if (!request.isUserInRole(role)) {
                return false;
            }
        }

        return true;
    }
}
