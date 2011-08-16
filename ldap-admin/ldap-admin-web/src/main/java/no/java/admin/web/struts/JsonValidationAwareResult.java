package no.java.admin.web.struts;

import com.googlecode.jsonplugin.JSONException;
import com.googlecode.jsonplugin.JSONUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.ValidationAware;
import static org.apache.struts2.StrutsStatics.HTTP_RESPONSE;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class JsonValidationAwareResult implements Result {
    public void execute(ActionInvocation invocation) throws Exception {
        ActionContext context = invocation.getInvocationContext();
        HttpServletResponse response = (HttpServletResponse) context.get(HTTP_RESPONSE);

        if ( "forbidden".equals(invocation.getResultCode())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }

        Object action = invocation.getAction();
        if (!(action instanceof ValidationAware)) {
            serialize(response, action);
            return;
        }

        ValidationAware validationAware = (ValidationAware) action;

        if (!validationAware.hasErrors()) {
            serialize(response, action);
            return;
        }

        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("fieldErrors", validationAware.getFieldErrors());
        map.put("actionErrors", validationAware.getActionErrors());
        map.put("actionMessages", validationAware.getActionMessages());

        String s = JSONUtil.serialize(map);
        System.out.println(s);

        serialize(response, map);
    }

    private void serialize(HttpServletResponse response, Object action) throws JSONException, IOException {
        String json = JSONUtil.serialize(action);

        String encoding = "UTF-8";
        response.setContentType("application/json;charset=" + encoding);
        response.setContentLength(json.getBytes(encoding).length);
        PrintWriter out = response.getWriter();
        out.print(json);
    }
}
