package no.java.admin.web.test;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class BasicWebTest extends AbstractWebTest {

    public void testUserLogin() {
        setBaseUrl();

        assertLinkPresent("login");
        assertLinkPresent("userRequestShowForm");
        clickLink("login");

        setTextField("j_username", "usr");
        setTextField("j_password", "123");
        submit();

        assertTextPresent("Logged in as "); // TODO: figure out the name of the user "usr"

        assertLinkPresent("logout");
        assertLinkPresent("userShow");
        assertLinkPresent("userRequestShowForm");
        assertLinkNotPresent("userRequestList");

        clickLink("logout");
        assertLinkPresent("login");
    }

    public void testAdminUserLogin() {
        setBaseUrl();

        assertLinkPresent("login");
        assertLinkPresent("userRequestShowForm");
        clickLink("login");

        setTextField("j_username", "trygvis");
        setTextField("j_password", "123");
        submit();

        assertTextPresent("Logged in as "); // TODO: figure out the name of the user "usr"

        assertLinkPresent("logout");
        assertLinkPresent("userShow");
        assertLinkPresent("userRequestShowForm");
        assertLinkPresent("userRequestList");

        clickLink("logout");
        assertLinkPresent("login");
    }
}
