package no.java.admin.web.action;

import no.java.admin.web.services.AuthenticationService;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AuthenticationAction {

    private boolean error;

    private AuthenticationService authenticationService;

    public AuthenticationAction(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    // Log in is handled by JAAS

    public String showLogin() {
        return "show-login";
    }

    public String logout() {
        authenticationService.logout();

        return "logout";
    }

    // -----------------------------------------------------------------------
    // Web Properties
    // -----------------------------------------------------------------------

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
