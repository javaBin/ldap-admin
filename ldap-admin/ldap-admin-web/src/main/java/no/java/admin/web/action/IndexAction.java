package no.java.admin.web.action;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.conversion.annotations.Conversion;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
@Conversion()
public class IndexAction extends ActionSupport {

    public String execute() throws Exception {
        return "index";
    }
}
