package no.java.admin.web.services;

import no.java.admin.web.events.LogoutEvent;
import no.java.admin.web.struts.JavaBinSecurityInterceptor;
import no.java.core.EventMonitor;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class AuthenticationService {

    private EventMonitor monitor;

    public void logout() {
        HttpServletRequest request = ServletActionContext.getRequest();

        HttpSession session = request.getSession(false);

        session.setAttribute(JavaBinSecurityInterceptor.INVALIDATE_SESSION, Boolean.TRUE);
    }

    public void filterLogout() {
        HttpServletRequest request = ServletActionContext.getRequest();

        Principal principal = request.getUserPrincipal();

        if (principal != null) {
            monitor.dispatchEvent(new LogoutEvent(principal.getName()));
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    // -----------------------------------------------------------------------
    // Properties
    // -----------------------------------------------------------------------

    public String getRemoteUser() {
        return ServletActionContext.getRequest().getRemoteUser();
    }

    public boolean isLoggedIn() {
        return getRemoteUser() != null;
    }

    // -----------------------------------------------------------------------
    // Spring
    // -----------------------------------------------------------------------

    public void setMonitor(EventMonitor monitor) {
        this.monitor = monitor;
    }
}
