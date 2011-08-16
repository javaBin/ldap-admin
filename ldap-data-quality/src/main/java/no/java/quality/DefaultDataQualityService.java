package no.java.quality;

import no.java.core.UserDirObjectFactory;
import no.java.core.UserNotFoundException;
import no.java.core.UserService;
import no.java.core.ldap.LdapTemplate;
import no.java.core.model.User;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.naming.InvalidNameException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultDataQualityService implements DataQualityService {

    private final static Logger log = Logger.getLogger(DefaultDataQualityService.class);

    private UserService userService;

    private LdapTemplate<User> userLdapTemplate;

    // -----------------------------------------------------------------------
    // DataQualityService
    // -----------------------------------------------------------------------

    public DataQualityReport createDataQualityReport() {
        try {
            List<SearchResult> list = userLdapTemplate.search(null);

            DataQualityReport report = new DataQualityReport();

            for (SearchResult searchResult : list) {
                // The parent/base object's name seem to be empty, skip it.
                if (StringUtils.isEmpty(searchResult.getName())) {
                    continue;
                }

                String name = searchResult.getNameInNamespace();

                DataQualityReport.UserReport userReport = new DataQualityReport.UserReport();
                userReport.setDn(name);
                userReport.setAttributes(searchResult.getAttributes());
                report.addUser(userReport);
            }

            for (DataQualityReport.UserReport userReport : report.getUsers()) {
                checkUid(userReport);
                checkRequiredAttributes(userReport);
                checkName(report, userReport);
                checkUserAttributes(userReport);
            }

            return report;
        } catch (RuntimeException e) {
            DataQualityReport report = new DataQualityReport();
            report.setException(e);
            return report;
        } catch (NamingException e) {
            DataQualityReport report = new DataQualityReport();
            report.setException(e);
            return report;
        }
    }

    private void checkUid(DataQualityReport.UserReport userReport) {
        String dn = userReport.getDn();

        try {
            userService.getUserByDn(dn);
        } catch (UserNotFoundException e) {
            userReport.addMessage("Invalid DN: '" + dn + "'.");
        }

        String uid;

        try {
            LdapName name = new LdapName(dn);

//                System.out.println("name = " + name);
            Rdn rdn = name.getRdn(name.size() - 1);

            uid = rdn.getValue().toString();
        } catch (InvalidNameException e) {
            userReport.addMessage("The object name returned from user search was invalid: " + dn + ". Message: " + e.getMessage() + ".");

            return;
        }

        userReport.setUid(uid);

        try {
            userReport.setUser(userService.getUser(uid));
        } catch (UserNotFoundException e) {
            userReport.addMessage("Invalid uid: '" + uid + "'.");
        }
    }

    private void checkRequiredAttributes(DataQualityReport.UserReport userReport) throws NamingException {

        for (String attributeName : UserDirObjectFactory.REQUIRED_ATTRIBUTES) {
            Attribute attribute = userReport.getAttributes().get(attributeName);

            if (attribute == null) {
                userReport.addMessage("Missing required attribute '" + attributeName + "'.");

                continue;
            }

            if (attribute.size() != 1) {
                userReport.addMessage("Invalid number of values on attribute '" + attributeName + "'. Expected 1, got " + attribute.size() + ".");
            }

            attribute.get(0).toString();
        }
    }

    private void checkName(DataQualityReport report, DataQualityReport.UserReport user) {
        List<SearchResult> searchResults = userLdapTemplate.search("objectClass=*");

        for (SearchResult searchResult : searchResults) {
            Attributes attributes = searchResult.getAttributes();

            try {
                Attribute cnAttribute = attributes.get("cn");
                if (cnAttribute == null) {
                    continue;
                }

                String cn = cnAttribute.get().toString();

                if (cn.contains("??")) {
                    user.addMessage("Common name contain funny characters.");
                }
            } catch (NamingException e) {
                report.getTechnicalErrors().add("Error while searching for objectClass=*");
            }
        }
    }

    private void checkUserAttributes(DataQualityReport.UserReport userReport) {
        User user = userReport.getUser();

        log.info("Checking " + userReport.getDn());

        if (user == null) {
            log.info("No user object for " + userReport.getDn());
            return;
        }

        if (StringUtils.isEmpty(user.getFirstName())) {
            userReport.addMessage("Missing attribute: first name");
        }

        if (StringUtils.isEmpty(user.getLastName())) {
            userReport.addMessage("Missing attribute: last name");
        }

        if (StringUtils.isEmpty(user.getMail())) {
            userReport.addMessage("Missing attribute: mail");
        }

        if (StringUtils.isEmpty(user.getMobilePhoneNumber())) {
            userReport.addMessage("Missing attribute: mobile phone number");
        }
    }

    // -----------------------------------------------------------------------
    // Spring
    // -----------------------------------------------------------------------

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setUserLdapTemplate(LdapTemplate<User> userLdapTemplate) {
        this.userLdapTemplate = userLdapTemplate;
    }
}
