package no.java.security.test;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultMyService implements MyService {
    public boolean aCalled;
    public boolean bCalled;

    public void methodA() {
        System.err.println("DefaultMyService.methodA");
        aCalled = true;
    }

    public void methodB() {
        System.err.println("DefaultMyService.methodB");
        bCalled = true;
    }
}
