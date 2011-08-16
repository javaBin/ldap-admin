package no.java.admin.web.test;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class RequestUserWebTest extends AbstractWebTest {

    private static final String FIRST_NAME = "Duke";
    private static final String LAST_NAME = "Dukeson";
    private static final String MAIL = "duke@dukeson.com";
    private static final String MOBILE_PHONE_NUMBER = "+1 123 345 678";
    private static final String MAIL_VALIDATION_ERROR_MESSAGE = "Mail is a required field";
    private static final String LAST_NAME_VALIDATION_ERROR_MESSAGE = "Last name is a required field";
    private static final String FIRST_NAME_VALIDATION_ERROR_MESSAGE = "First name is a required field";
    private static final String VALIDATE = "action:storeUserRequest!validateRequest";
    private static final String DO_IT = "action:storeUserRequest!store";
    private static final String CHANGE = "action:userRequest!showForm";

    public void testRequestUser() {
        setBaseUrl();

        beginAt("/");
        clickLink("userRequestShowForm");

        assertTextNotPresent("Uid");
        assertTextPresent("First name");

        // -----------------------------------------------------------------------
        // Click the empty form
        // -----------------------------------------------------------------------

        assertTextNotPresent("Uid");
        assertTextFieldEquals("firstName", "");
        assertTextFieldEquals("lastName", "");
        assertTextFieldEquals("mail", "");
        assertTextFieldEquals("mobilePhoneNumber", "");

        submit(VALIDATE);
        assertTextNotPresent("Uid");
        assertTextPresent(FIRST_NAME_VALIDATION_ERROR_MESSAGE);
        assertTextPresent(LAST_NAME_VALIDATION_ERROR_MESSAGE);
        assertTextPresent(MAIL_VALIDATION_ERROR_MESSAGE);

        // -----------------------------------------------------------------------
        // Fill in some values
        // -----------------------------------------------------------------------

        clickLink("userRequestShowForm");
        assertElementNotPresent("uid");
        assertTextNotPresent("Uid");
        setTextField("firstName", FIRST_NAME);
        setTextField("lastName", LAST_NAME);
        setTextField("mail", MAIL);
        setTextField("mobilePhoneNumber", MOBILE_PHONE_NUMBER);
        submit(VALIDATE);

        // -----------------------------------------------------------------------
        // Check that the confirmation page is equal
        // -----------------------------------------------------------------------

        assertTextInElement("uid", "duke.dukeson");
        assertTextInElement("firstName", FIRST_NAME);
        assertTextInElement("lastName", LAST_NAME);
        assertTextInElement("mail", MAIL);
        assertTextInElement("mobilePhoneNumber", MOBILE_PHONE_NUMBER);

        assertSubmitButtonPresent(DO_IT);
        assertSubmitButtonPresent(CHANGE);

        submit(CHANGE);

        // -----------------------------------------------------------------------
        // Go back to change some values
        // -----------------------------------------------------------------------

        assertTextFieldEquals("firstName", FIRST_NAME);
        assertTextFieldEquals("lastName", LAST_NAME);
        assertTextFieldEquals("mail", MAIL);
        assertTextFieldEquals("mobilePhoneNumber", MOBILE_PHONE_NUMBER);

        setTextField("firstName", FIRST_NAME + " D.");
        submit(VALIDATE);

        // -----------------------------------------------------------------------
        // Submit the request
        // -----------------------------------------------------------------------

        assertTextInElement("uid", "duke.d.dukeson");
        assertTextInElement("firstName", FIRST_NAME + " D.");
        submit(DO_IT);

        // -----------------------------------------------------------------------
        // Validate the confirmation page
        // -----------------------------------------------------------------------

        assertTextInElement("uid", "duke.d.dukeson");
        assertTextInElement("firstName", FIRST_NAME + " D.");
        assertTextInElement("lastName", LAST_NAME);
        assertTextInElement("mail", MAIL);
        assertTextInElement("mobilePhoneNumber", MOBILE_PHONE_NUMBER);

        assertSubmitButtonPresent("action:userRequest!showBlankForm", "New request");
    }
}
