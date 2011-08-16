package no.java.admin.services;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

import java.io.File;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class UidServiceTest extends AbstractDependencyInjectionSpringContextTests {

    private UidService uidService;

    public void testBasic() {
        assertNull(uidService.generateUid(null, null));
        assertNull(uidService.generateUid("Trygve", null));
        assertNull(uidService.generateUid(null, "Laugstol"));

        assertEquals("trygve.laugstol", uidService.generateUid("Trygve", "Laugstol"));

        assertEquals("mr.duke.dukeson", uidService.generateUid("Mr Duke", "Dukeson"));

        assertEquals("duke.d.dukeson", uidService.generateUid("Duke D.", "Dukeson"));

        // Test names with space
        assertEquals("f1.f2.e1.e2", uidService.generateUid("F1 f2", "E1 E2"));

        // LATIN SMALL LETTER Y WITH DIAERESIS and LATIN SMALL LETTER O WITH STROKE
        assertEquals("y.o", uidService.generateUid("\u00ff", "\u00f8"));

        assertNull(uidService.generateUid("Trygve", "Laugst\u0248l"));
    }

    public void setUidService(UidService uidService) {
        this.uidService = uidService;
    }

    protected String[] getConfigLocations() {
        if (System.getProperty("basedir") == null) {
            System.setProperty("basedir", new File("").getAbsolutePath());
        }
        return new String[]{"classpath*:spring.xml"};
    }
}
