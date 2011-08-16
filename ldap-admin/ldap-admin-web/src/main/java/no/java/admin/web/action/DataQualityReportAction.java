package no.java.admin.web.action;

import com.opensymphony.xwork2.ActionSupport;
import no.java.quality.DataQualityReport;
import no.java.quality.DataQualityService;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DataQualityReportAction extends ActionSupport {

    private DataQualityService dataQualityService;

    private DataQualityReport dataQualityReport;

    public String run() {
        dataQualityReport = dataQualityService.createDataQualityReport();

        return "data-quality-report-detail";
    }

    // -----------------------------------------------------------------------
    // Properties
    // -----------------------------------------------------------------------

    public DataQualityReport getDataQualityReport() {
        return dataQualityReport;
    }

    // -----------------------------------------------------------------------
    // Spring Properties
    // -----------------------------------------------------------------------

    public void setDataQualityService(DataQualityService dataQualityService) {
        this.dataQualityService = dataQualityService;
    }
}
