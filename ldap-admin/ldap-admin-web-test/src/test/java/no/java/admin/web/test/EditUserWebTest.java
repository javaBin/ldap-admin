package no.java.admin.web.test;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class EditUserWebTest extends AbstractWebTest {
    public void testAuthenticatedUser() {
        setBaseUrl();

        new LoginPage("are.tysnes", "123");

        clickLink("userList");

        clickLink("show-kristian.berg");

        UserPage userPage = new UserPage();

        userPage.assertIsReadOnly();

        clickLink("logout");
    }

    public void testSelfUser() {
        setBaseUrl();

        new LoginPage("are.tysnes", "123");

        clickLink("userList");

        clickLink("show-are.tysnes");

        UserPage userPage = new UserPage();

        userPage.assertIsReadWrite();

        clickLink("logout");
    }
}
