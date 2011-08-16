package no.java.quality;

import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DataQualityReportTest extends AbstractDependencyInjectionSpringContextTests {
    private DataQualityService dataQualityService;

    protected String[] getConfigLocations() {
        return new String[]{
            "classpath*:spring.xml",
        };
    }

    public void setDataQualityService(DataQualityService dataQualityService) {
        this.dataQualityService = dataQualityService;
    }

    public void testBasic() {
        System.out.println("Creating report");
        DataQualityReport report = dataQualityService.createDataQualityReport();

        Exception exception = report.getException();
        if (exception != null) {
            exception.printStackTrace();
            fail(exception.getMessage());
        }

        System.out.println("Techincal errors, count: " + report.getTechnicalErrors().size());
        for (String error : report.getTechnicalErrors()) {
            System.out.println(error);
        }

        System.out.println("Users, count: " + report.getUsers().size());
        for (DataQualityReport.UserReport userReport : report.getUsers()) {
            System.out.println("dn: " + userReport.getDn());
            System.out.println("uid: " + userReport.getUid());
            for (String message : userReport.getMessages()) {
                System.out.println("message: " + message);
            }
        }
    }
}
