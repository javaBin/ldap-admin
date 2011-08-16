package no.java.admin.web.struts;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import no.java.admin.web.services.AuthenticationService;
import no.java.admin.web.services.AuthorizationService;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpSession;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class JavaBinSecurityInterceptor extends AbstractInterceptor {
    public static final String INVALIDATE_SESSION = JavaBinSecurityInterceptor.class.getName() + "-invalidate-session";

    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;

    public JavaBinSecurityInterceptor(AuthenticationService authenticationService, AuthorizationService authorizationService) {
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        String actionName = invocation.getProxy().getActionName();
        String method = invocation.getProxy().getMethod();

        System.out.println("Class: " + invocation.getAction().getClass().getName() + ", action=" + actionName + "!" + method);

        invocation.getStack().getContext().put("authentication", authenticationService);
        invocation.getStack().getContext().put("authorization", authorizationService);

        String result = invocation.invoke();

        HttpSession session = ServletActionContext.getRequest().getSession(false);

        if (session != null) {
            if (Boolean.TRUE.equals(session.getAttribute(INVALIDATE_SESSION))) {
                authenticationService.filterLogout();
            }
        }

        return result;
    }
}
