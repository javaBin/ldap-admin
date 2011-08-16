package no.java.admin.web.action;

import com.opensymphony.xwork2.ActionSupport;
import no.java.core.model.User;
import no.java.core.UserService;

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class SearchAction extends ActionSupport {

    private String query;

    private String searchMessage;

    private List<User> users;

    private UserService userService;

    public SearchAction(UserService userService) {
        this.userService = userService;
    }

    public String execute() throws Exception {
        if (StringUtils.isEmpty(query)) {
            return "index";
        }

        users = userService.search(query);

        if (users.size() == 0) {
            searchMessage = "No users found";

            return "index";
        }

        return "user-search";
    }

    // -----------------------------------------------------------------------
    // Properties
    // -----------------------------------------------------------------------

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQueryMessage() {
        return searchMessage;
    }

    public void setQueryMessage(String searchMessage) {
        this.searchMessage = searchMessage;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
