package no.java.security.test;

import javax.security.auth.Subject;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SecureMyService implements InvocationHandler {

    private final Subject subject;
    private final Object securedObject;

    public SecureMyService(Subject subject, Object securedObject) {
        this.subject = subject;
        this.securedObject = securedObject;
    }

    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        System.err.println("SecureMyService.invoke: " + securedObject.getClass().getName() + ":" + method.getName());

        System.getSecurityManager().checkPermission(new GenericServicePermission(securedObject.getClass().getName(), method.getName()));

        return method.invoke(securedObject, args);
    }
}
