package no.java.admin.web.action;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ReportAction extends ActionSupport {

    public String list() {
        return "report-list";
    }
}
