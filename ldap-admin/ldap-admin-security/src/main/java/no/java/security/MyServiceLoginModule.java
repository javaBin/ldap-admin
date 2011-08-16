package no.java.security;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import java.security.Principal;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class MyServiceLoginModule implements LoginModule {
    private Subject subject;
    private CallbackHandler callbackHandler;
    private Map<String, ?> sharedState;
    private Map<String, ?> options;

    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        this.subject = subject;
        this.callbackHandler = callbackHandler;
        this.sharedState = sharedState;
        this.options = options;

        System.err.println("MyServiceLoginModule.initialize");
        System.err.println("subject = " + subject);
        System.err.println("sharedState = " + sharedState);
        System.err.println("options = " + options);
    }

    public boolean login() throws LoginException {
        System.err.println("MyServiceLoginModule.login");
        System.err.println("subject = " + subject);

        for (Principal principal : subject.getPrincipals()) {
            if(principal.getName().equals("ldap-admin")) {
            }
        }

        return true;
    }

    public boolean commit() throws LoginException {
        System.err.println("MyServiceLoginModule.commit");
        System.err.println("subject = " + subject);
        return true;
    }

    public boolean abort() throws LoginException {
        System.err.println("MyServiceLoginModule.abort");
        return true;
    }

    public boolean logout() throws LoginException {
        System.err.println("MyServiceLoginModule.logout");
        return true;
    }
}
