package no.java.core;

import no.java.core.ldap.LdapTemplate;
import no.java.core.model.Group;
import no.java.core.model.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultUserService implements UserService, InitializingBean {
    private final static Logger log = Logger.getLogger(DefaultUserService.class);

    private MessageDigest digest;

    private Charset charset;

    // -----------------------------------------------------------------------
    // UserService Implementation
    // -----------------------------------------------------------------------

    public List<User> getUsers() {
        return userLdapTemplate.searchObject("(objectclass=inetOrgPerson)");
    }

    public User getUser(String uid) throws UserNotFoundException {
        assertNotEmptyArgument(uid, "uid");

        try {
            return userLdapTemplate.lookupObject(userLdapTemplate.makeRdn("uid", uid));
        } catch (NameNotFoundException e) {
            throw new UserNotFoundException("Could not find user with uid '" + uid + "'.", e);
        }
    }

    public User getUserByDn(String dn) throws UserNotFoundException {
        assertNotEmptyArgument(dn, "dn");

        try {
            return userLdapTemplate.lookupObject(userLdapTemplate.makeLdapName(dn));
        } catch (NameNotFoundException e) {
            throw new UserNotFoundException("Could not find user with dn '" + dn + "'.", e);
        }
    }

    /**
     * Method returns a <tt>User</tt> instance for a user whose <code>mail</code>
     * attribute is <code>email</code>.
     *
     * @return <tt>User</tt> instance or <code>null</code> if a matching user cannot be found.
     */
    public User getUserByMail(String mail) throws UserNotFoundException {
        assertNotEmptyArgument(mail, "mail");

        List<User> result = userLdapTemplate.searchObject("(mail={0})", new Object[]{mail}, null);

        if (result.isEmpty()) {
            throw new UserNotFoundException("Could not find user with mail='" + mail + "'.");
        }

        return result.get(0);
    }

    public User findUserByMail(String mail) {
        assertNotEmptyArgument(mail, "mail");

        List<User> result = userLdapTemplate.searchObject("(mail={0})", new Object[]{mail}, null);

        return result.isEmpty() ? null : result.get(0);
    }

    public String getDnByUid(String uid) throws UserNotFoundException {
        assertNotEmptyArgument(uid, "uid");

        try {
            LdapName dn = userLdapTemplate.makeDn("uid", uid);

            userLdapTemplate.lookupObject(dn);

            return dn.toString();
        } catch (NameNotFoundException e) {
            throw new UserNotFoundException("Could not find user with uid '" + uid + "'.", e);
        }
    }

    public void saveUser(User user) {
        assertNotEmptyArgument(user, "user");
        assertNotEmptyArgument(user.getDn(), "user.dn");

        // TODO: This should be an event
        log.info("Storing user: " + user);

        userLdapTemplate.rebindObject(userLdapTemplate.makeRdn("uid", user.getUid()), user);

        if (user.getGroups() != null) {
            saveGroups(user.getUid(), user.getGroups());
        }
    }

    public void saveGroups(String uid, List<String> newGroups) {
        assertNotEmptyArgument(uid, "uid");
        assertNotEmptyArgument(newGroups, "newGroups");

        List<String> currentGroups = findGroupsByMember(uid);

        List<String> added = new ArrayList<String>(newGroups);
        added.removeAll(currentGroups);

        List<String> removed = new ArrayList<String>(currentGroups);
        removed.removeAll(newGroups);

        String dn = userLdapTemplate.makeDn("uid", uid).toString();

        log.info("DefaultUserService.saveGroups: uid: " + uid + ", dn: " + dn);
        log.info("currentGroups = " + currentGroups);
        log.info("newGroups = " + newGroups);
        log.info("added = " + added);
        log.info("removed = " + removed);

        for (String group : added) {
            Rdn rdn = groupLdapTemplate.makeRdn("cn", group);

            Attributes attributes = groupLdapTemplate.lookup(rdn, "uniqueMember");
            Attribute attribute = attributes.get("uniqueMember");
            attribute.add(dn);

            log.info("Members of '" + group + "' after adding = " + attribute);

            groupLdapTemplate.modifyAttributes(rdn, new ModificationItem[]{
                new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attribute)
            });
        }

        for (String group : removed) {
            Rdn rdn = groupLdapTemplate.makeRdn("cn", group);

            Attributes attributes = groupLdapTemplate.lookup(rdn, "uniqueMember");
            Attribute attribute = attributes.get("uniqueMember");

            // -----------------------------------------------------------------------
            // Attribute.remove() won't work as it is case-sensitive
            // -----------------------------------------------------------------------

            try {
                NamingEnumeration<?> e = attribute.getAll();
                int i = 0;
                while (e.hasMoreElements()) {
                    String element = (String) e.nextElement();

                    if (element.equalsIgnoreCase(dn)) {
                        attribute.remove(i);
                        break;
                    }
                    i++;
                }
            } catch (NamingException ne) {
                throw new RuntimeException(ne);
            }

            groupLdapTemplate.modifyAttributes(rdn, new ModificationItem[]{
                new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attribute)
            });
        }
    }

    public User createUser(User user) {
        assertNotEmptyArgument(user, "user");
        assertNotEmptyArgument(user.getUid(), "user.uid");

        String password = passwordGenerator.generatePassword();
        String hashedPasword = createDigestedPassword(password);

        String uid = user.getUid();
        user.setPassword(hashedPasword);
        Rdn uidRdn = userLdapTemplate.makeRdn("uid", uid);

        userLdapTemplate.bindObject(uidRdn, user);

        try {
            User createdUser = userLdapTemplate.lookupObject(uidRdn);

            createdUser.setPassword(password);

            return createdUser;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Error while looking up newly created object with uid='" + uid + "'.", e);
        }
    }

    public void deleteUser(String uid) {
        assertNotEmptyArgument(uid, "uid");

        userLdapTemplate.unbind(userLdapTemplate.makeRdn("uid", uid));
    }

    public String resetPassword(String uid) {
        assertNotEmptyArgument(uid, "uid");

        String password = passwordGenerator.generatePassword();

        setPassword(uid, password);

        return password;
    }

    public void setPassword(String uid, String password) {
        assertNotEmptyArgument(uid, "uid");
        assertNotEmptyArgument(password, "password");

        String hashedPasword = createDigestedPassword(password);

        userLdapTemplate.modifyAttributes(userLdapTemplate.makeRdn("uid", uid), new ModificationItem[]{
            new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword", hashedPasword))
        });
    }

    public List<String> findGroupsByMember(String uid) {
        assertNotEmptyArgument(uid, "uid");

        String dn = userLdapTemplate.makeDn("uid", uid).toString();
        List<SearchResult> list = groupLdapTemplate.search("uniqueMember={0}", new Object[]{dn}, new String[]{"cn"});

        try {
            List<String> groups = new ArrayList<String>(list.size());
            for (SearchResult result : list) {
                Attribute attribute = result.getAttributes().get("cn");

                if (attribute == null || attribute.size() == 0) {
                    continue;
                }

                Object cn = attribute.get(0);

                if (!(cn instanceof String)) {
                    continue;
                }

                groups.add(cn.toString());
            }

            return groups;
        } catch (NamingException e) {
            throw new RuntimeException("Error while iterating result.", e);
        }
    }

    public List<User> search(String query) {
        assertNotEmptyArgument(query, "query");

        return userLdapTemplate.searchObject("(|(cn=*{0}*)(sn=*{0}*)(givenName=*{0}*))", new Object[]{query}, null);
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    protected String createDigestedPassword(String password) {
        byte[] data = charset.encode(password).array();

        byte[] fingerPrint = digest.digest(data);

        StringBuffer result = new StringBuffer();
        result.append('{');
        result.append(algorithm);
        result.append('}');
        result.append(new String(new Base64().encode(fingerPrint)));
        return result.toString();
    }

    private void assertNotEmptyArgument(String value, String arguemnt) {
        if (StringUtils.isEmpty(value)) {
            throw new IllegalArgumentException("Argument '" + arguemnt + "' is empty.");
        }
    }

    private void assertNotEmptyArgument(Object value, String arguemnt) {
        if (value == null) {
            throw new IllegalArgumentException("Argument '" + arguemnt + "' is empty.");
        }
    }

    // -----------------------------------------------------------------------
    // Properties
    // -----------------------------------------------------------------------

    private LdapTemplate<User> userLdapTemplate;

    public void setUserLdapTemplate(LdapTemplate<User> ldapTemplate) {
        this.userLdapTemplate = ldapTemplate;
    }

    private LdapTemplate<Group> groupLdapTemplate;

    public void setGroupLdapTemplate(LdapTemplate<Group> groupLdapTemplate) {
        this.groupLdapTemplate = groupLdapTemplate;
    }

    private PasswordGenerator passwordGenerator;

    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    private String algorithm;

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public void afterPropertiesSet() throws Exception {
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException nsae) {
            throw new IllegalArgumentException("No such algorithm available: " + algorithm);
        }

        charset = Charset.forName("UTF-8");
    }
}
