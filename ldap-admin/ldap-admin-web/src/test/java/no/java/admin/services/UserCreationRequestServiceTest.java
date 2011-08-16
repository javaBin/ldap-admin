package no.java.admin.services;

import junit.framework.TestCase;
import no.java.core.TechnicalException;
import no.java.core.UserService;
import no.java.core.model.User;
import org.apache.commons.io.FileUtils;
import static org.easymock.EasyMock.*;

import java.io.File;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class UserCreationRequestServiceTest extends TestCase {
    private DefaultUserCreationRequestService userCreationRequestService;

    public void setUp() throws Exception {
        userCreationRequestService = new DefaultUserCreationRequestService();

        File basedir = new File(System.getProperty("basedir", null), "target/user-requests");

        userCreationRequestService.setBasedir(basedir.getAbsolutePath());

        if (basedir.isDirectory()) {
            FileUtils.deleteDirectory(basedir);
        }

        assertTrue(basedir.mkdirs());
    }

    public void testLifecycle() throws Exception {
        // Arguments and return values
        User user = new User();
        user.setDn("dn");
        user.setFirstName("Trygve");
        user.setLastName("Laugst\u0248l");

        // Set up mock
        UserService userService = createStrictMock(UserService.class);
        expect(userService.createUser(isA(User.class))).andReturn(user);

        userCreationRequestService.setUserService(userService);
        userCreationRequestService.afterPropertiesSet();

        replay(userService);

        // DO IT!
        List<UserCreationRequest> creationRequests = userCreationRequestService.getRequests();

        assertNotNull(creationRequests);
        assertEquals(0, creationRequests.size());

        String requestId = userCreationRequestService.storeRequest(user);

        UserCreationRequest request = userCreationRequestService.getRequest(requestId);
        assertNotNull(request);
        assertEquals(requestId, request.getRequestId());
        assertEquals(user, request.getUser());

        creationRequests = userCreationRequestService.getRequests();
        assertEquals(1, creationRequests.size());

        try {
            userCreationRequestService.approveRequest(null);
            fail("Expected TechnicalException");
        } catch (TechnicalException e) {
            // expected
        }

        UserCreationResult result = userCreationRequestService.approveRequest(requestId);
        assertNotNull(result);
        assertEquals(user, result.getUser());

        try {
            userCreationRequestService.approveRequest(requestId);
            fail("Expected TechnicalException");
        } catch (TechnicalException e) {
            // expected
        }

        requestId = userCreationRequestService.storeRequest(user);

        userCreationRequestService.rejectRequest(requestId);

        try {
            userCreationRequestService.rejectRequest(requestId);
            fail("Expected TechnicalException");
        } catch (TechnicalException e) {
            // expected
        }

        verify();
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    private void assertEquals(User expected, User actual) {
        if (expected == null) {
            assertNull("The user was expected to be null.", actual);
            return;
        }

        assertNotNull("The actual user was null.", actual);

        assertEquals(expected.getDn(), actual.getDn());
        assertEquals(expected.getUid(), actual.getUid());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getMail(), actual.getMail());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getMobilePhoneNumber(), actual.getMobilePhoneNumber());
    }
}
