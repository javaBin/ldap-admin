package no.java.admin.web.test;

import net.sourceforge.jwebunit.junit.WebTestCase;
import no.java.admin.web.LdapAdminWebEmbedder;
import org.codehaus.plexus.PlexusTestCase;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AbstractWebTest extends WebTestCase {
    protected LdapAdminWebEmbedder embedder;
    private int port;

    protected void setUp() throws Exception {
        embedder = new LdapAdminWebEmbedder(PlexusTestCase.getTestFile("."), 8080);

        embedder.start();

        port = embedder.getPort();

        System.out.println("http://localhost:" + embedder.getPort() + "/ldap-admin");
    }

    protected void tearDown() throws Exception {
        embedder.stop();
    }

    public void setBaseUrl() {
        setScriptingEnabled(false);
        getTestContext().setBaseUrl("http://localhost:" + port + "/ldap-admin");
        beginAt("/");
    }

    public int getPort() {
        return port;
    }

    class LoginPage {
        public LoginPage(String username, String password) {
            clickLink("login");
            setTextField("j_username", username);
            setTextField("j_password", password);
            submit();
        }
    }

    class UserPage {
        public void assertIsReadOnly() {
            assertTablePresent("user_ro");
            assertFormElementPresent("uid"); // This will always be present as a hidden field
            assertFormElementNotPresent("firstName");
            assertFormElementNotPresent("lastName");
            assertFormElementNotPresent("mail");
            assertFormElementNotPresent("mobilePhoneNumber");

            assertFormNotPresent("password_form");

            assertTablePresent("groups");
        }

        public void assertIsReadWrite() {
            assertTablePresent("user_rw");

            assertFormPresent("user_form");
            assertFormElementPresent("uid");
            assertFormElementPresent("user.firstName");
            assertFormElementPresent("user.lastName");
            assertFormElementPresent("user.mail");
            assertFormElementPresent("user.mobilePhoneNumber");

            assertFormPresent("groups_form");
            assertFormElementPresent("uid");
            assertTablePresent("groups");

            assertFormPresent("password_form");
        }
    }
}
