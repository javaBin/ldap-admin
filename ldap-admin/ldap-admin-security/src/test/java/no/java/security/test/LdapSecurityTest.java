package no.java.security.test;

import junit.framework.TestCase;
import org.apache.geronimo.security.realm.providers.PasswordCallbackHandler;
import org.codehaus.plexus.PlexusTestCase;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import java.lang.reflect.Proxy;
import java.security.AccessControlException;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.util.Collections;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LdapSecurityTest extends TestCase {
    protected void setUp() throws Exception {
        System.setProperty("java.security.auth.login.config", PlexusTestCase.getTestPath("src/test/resources/login.config"));
        System.setProperty("java.security.policy", PlexusTestCase.getTestPath("src/test/resources/security.policy"));
    }

    public void testBasic() throws Exception {
        SecurityManager s = new SecurityManager();
        System.setSecurityManager(s);

        CallbackHandler callbackHandler = new PasswordCallbackHandler("trygvis", "123".toCharArray());

        LoginContext context = new LoginContext("ldap-admin", callbackHandler);

        context.login();

        Subject subject = context.getSubject();

        System.err.println("subject = " + subject);
        System.err.println("subject.getPrincipals()");
        for (Principal principal : subject.getPrincipals()) {
            System.err.println("principal = " + principal);
            System.err.println("principal.getClass().getName() = " + principal.getClass().getName());
        }
        System.err.println("subject.getPrivateCredentials()");
        for (Object o : subject.getPrivateCredentials()) {
            System.err.println("o = " + o);
        }
        System.err.println("subject.getPublicCredentials()");
        for (Object o : subject.getPublicCredentials()) {
            System.err.println("o = " + o);
        }

        GenericServicePermission methodAPermission = new GenericServicePermission(MyService.class.getName(), "methodA");
        AccessController.getContext().checkPermission(methodAPermission);

        DefaultMyService service = new DefaultMyService();

        final MyService myService = (MyService) Proxy.newProxyInstance(MyService.class.getClassLoader(),
            new Class<?>[]{MyService.class},
            new SecureMyService(subject, service));

        // -----------------------------------------------------------------------
        // The shit
        // -----------------------------------------------------------------------

        System.err.println("the shit");

        PrivilegedExceptionAction<Object> action = new PrivilegedExceptionAction<Object>() {
            public Object run() throws Exception {
                myService.methodA();

                try {
                    myService.methodB();
                    fail("Expected AccessControlException");
                } catch (AccessControlException e) {
                    // expected
                }

                return null;
            }
        };

        Subject.doAs(subject, action);

        assertTrue(service.aCalled);
        assertFalse(service.bCalled);

        context.logout();
    }
}
