package no.java.core;

import no.java.core.model.User;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class UserServiceTest extends AbstractDependencyInjectionSpringContextTests {
    private UserService userService;

    protected String[] getConfigLocations() {
        return new String[]{
            "classpath*:spring.xml",
        };
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void testSearch() {
        List<User> userList = userService.search("lind");

        assertNotNull(userList);
        assertEquals(1, userList.size());

        User user = userList.get(0);
        assertEquals("bard.lind", user.getUid());
    }

    public void testLookup() throws Exception {
        User trygvis = userService.getUser("trygvis");

        assertNotNull(trygvis);
        assertEquals("trygvis", trygvis.getUid());
        assertEquals("uid=trygvis,ou=People,dc=java,dc=no", trygvis.getDn());
        assertEquals("Trygve", trygvis.getFirstName());

        try {
            userService.getUser("tullball");
            fail("Expected UserNotFoundException");
        } catch (UserNotFoundException e) {
            // expected
        }

        try {
            userService.getUserByDn("cn=tullball,dc=java,dc=no");
            fail("Expected UserNotFoundException");
        } catch (UserNotFoundException e) {
            // expected
        }

        try {
            userService.getUserByMail("tullball");
            fail("Expected UserNotFoundException");
        } catch (UserNotFoundException e) {
            // expected
        }

        assertNull(userService.findUserByMail("trygvis@java.no"));
    }
}
