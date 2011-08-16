package no.java.admin.jaas;

import org.mortbay.jetty.plus.jaas.spi.UserInfo;
import org.mortbay.jetty.plus.jaas.spi.AbstractLoginModule;
import org.mortbay.jetty.plus.jaas.callback.ObjectCallback;
import org.mortbay.jetty.security.Credential;
import org.mortbay.log.Log;

import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.AuthenticationException;
import javax.security.auth.login.LoginException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.Subject;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="mailto:trygve.laugstol@arktekk.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LdapLoginModule extends AbstractLoginModule
{
    /**
     * hostname of the ldap server
     */
    private String _hostname;

    /**
     * port of the ldap server
     */
    private int _port;

    /**
     * Context.SECURITY_AUTHENTICATION
     */
    private String _authenticationMethod;

    /**
     * Context.INITIAL_CONTEXT_FACTORY
     */
    private String _contextFactory;

    /**
     * root DN used to connect to
     */
    private String _bindDn;

    /**
     * password used to connect to the root ldap context
     */
    private String _bindPassword;

    /**
     * object class of a user
     */
    private String _userObjectClass = "inetOrgPerson";

    /**
     * attribute that the principal is located
     */
    private String _userRdnAttribute = "uid";

    /**
     * attribute that the principal is located
     */
    private String _userIdAttribute = "cn";

    /**
     * name of the attribute that a users password is stored under
     * <p/>
     * NOTE: not always accessible, see force binding login
     */
    private String _userPasswordAttribute = "userPassword";

    /**
     * base DN where users are to be searched from
     */
    private String _userBaseDn;

    /**
     * base DN where role membership is to be searched from
     */
    private String _roleBaseDn;

    /**
     * object class of roles
     */
    private String _roleObjectClass = "groupofuniquenames";

    /**
     * name of the attribute that a username would be under a role class
     */
    private String _roleMemberAttribute = "uniqueMember";

    /**
     * the name of the attribute that a role would be stored under
     */
    private String _roleNameAttribute = "roleName";

    private boolean _debug;

    /**
     * if the getUserInfo can pull a password off of the user then
     * password comparison is an option for authn, to force binding
     * login checks, set this to true
     */
    private boolean _forceBindingLogin = false;

    private DirContext _rootContext;

    /**
     * get the available information about the user
     * <p/>
     * for this LoginModule, the credential can be null which will result in a
     * binding ldap authentication scenario
     * <p/>
     * roles are also an optional concept if required
     *
     * @param username
     * @return
     * @throws Exception
     */
    public UserInfo getUserInfo(String username) throws Exception
    {
        String pwdCredential = getUserCredentials(username);

        if (pwdCredential == null)
        {
            return null;
        }

        pwdCredential = convertCredentialLdapToJetty(pwdCredential);

        //String md5Credential = Credential.MD5.digest("foo");
        //byte[] ba = digestMD5("foo");
        //System.out.println(md5Credential + "  " + ba );
        Credential credential = Credential.getCredential(pwdCredential);
        List roles = getUserRoles(username);

        return new UserInfo(username, credential, roles);
    }

    protected String doRFC2254Encoding(String inputString)
    {
        StringBuffer buf = new StringBuffer(inputString.length());
        for (int i = 0; i < inputString.length(); i++)
        {
            char c = inputString.charAt(i);
            switch (c)
            {
                case '\\':
                    buf.append("\\5c");
                    break;
                case '*':
                    buf.append("\\2a");
                    break;
                case '(':
                    buf.append("\\28");
                    break;
                case ')':
                    buf.append("\\29");
                    break;
                case '\0':
                    buf.append("\\00");
                    break;
                default:
                    buf.append(c);
                    break;
            }
        }
        return buf.toString();
    }

    /**
     * attempts to get the users credentials from the users context
     * <p/>
     * NOTE: this is not an user authenticated operation
     *
     * @param username
     * @return
     * @throws javax.security.auth.login.LoginException
     */
    private String getUserCredentials(String username) throws LoginException
    {
        String ldapCredential = null;

        SearchControls ctls = new SearchControls();
        ctls.setCountLimit(1);
        ctls.setDerefLinkFlag(true);
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String filter = "(&(objectClass=" + _userObjectClass + ")(" + _userIdAttribute + "=" + username + "))";

        Log.debug("Searching for users with filter: \'" + filter + "\'" + " from base dn: " + _userBaseDn);

        try
        {
            NamingEnumeration results = _rootContext.search(_userBaseDn, filter, ctls);

            Log.debug("Found user?: " + results.hasMoreElements());

            if (!results.hasMoreElements())
            {
                throw new LoginException("User not found.");
            }

            SearchResult result = findUser(username);

            Attributes attributes = result.getAttributes();

            Attribute attribute = attributes.get(_userPasswordAttribute);
            if (attribute != null)
            {
                try
                {
                    byte[] value = (byte[]) attribute.get();

                    ldapCredential = new String(value);
                }
                catch (NamingException e)
                {
                    Log.debug("no password available under attribute: " + _userPasswordAttribute);
                }
            }
        }
        catch (NamingException e)
        {
            throw new LoginException("Root context binding failure.");
        }

        Log.debug("user cred is: " + ldapCredential);

        return ldapCredential;
    }

    /**
     * attempts to get the users roles from the root context
     * <p/>
     * NOTE: this is not an user authenticated operation
     *
     * @param username
     * @return
     * @throws LoginException
     */
    private List getUserRoles(String username) throws LoginException, NamingException
    {
        ArrayList roleList = new ArrayList();

        if (_roleBaseDn == null)
        {
            return roleList;
        }

        SearchControls ctls = new SearchControls();

        ctls.setDerefLinkFlag(true);
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String userDn = _userRdnAttribute + "=" + username + "," + _userBaseDn;
        String filter = "(&(objectClass=" + _roleObjectClass + ")(" + _roleMemberAttribute + "=" + userDn + "))";
        //filter  = doRFC2254Encoding(filter)
        NamingEnumeration results = _rootContext.search(_roleBaseDn, filter, ctls);

        while (results.hasMoreElements())
        {
            SearchResult result = (SearchResult)results.nextElement();

            Attributes attributes = result.getAttributes();

            if (attributes != null)
            {
                Attribute roleAttribute = attributes.get(_roleNameAttribute);

                if (roleAttribute != null)
                {
                    NamingEnumeration roles = roleAttribute.getAll();
                    while (roles.hasMore())
                    {
                        String roleName = (String) roles.next();
                        roleList.add(roleName);
                    }
                }
            }
        }

        Log.debug("Found user roles: " + roleList.toString());

        return roleList;
    }

    /**
     * since ldap uses a context bind for valid authentication checking, we override login()
     * <p/>
     * if credentials are not available from the users context or if we are forcing the binding check
     * then we try a binding authentication check, otherwise if we have the users encoded password then
     * we can try authentication via that mechanic
     *
     * @return
     * @throws LoginException
     */
    public boolean login() throws LoginException
    {
        try
        {
            if (getCallbackHandler() == null)
            {
                throw new LoginException("No callback handler");
            }

            Callback[] callbacks = configureCallbacks();
            getCallbackHandler().handle(callbacks);

            String webUserName = ((NameCallback) callbacks[0]).getName();
            Object webCredential = ((ObjectCallback) callbacks[1]).getObject();

            if (webUserName == null || webCredential == null)
            {
                setAuthenticated(false);
                return isAuthenticated();
            }

            if (_forceBindingLogin)
            {
                return bindingLogin(webUserName, webCredential);
            }

            // This sets read and the credential
            UserInfo userInfo = getUserInfo(webUserName);

            if( userInfo == null) {
                setAuthenticated(false);
                return false;
            }

            setCurrentUser(new AbstractLoginModule.JAASUserInfo(userInfo));

            if (webCredential instanceof String)
            {
                return credentialLogin(Credential.getCredential((String) webCredential));
            }

            return credentialLogin(webCredential);
        }
        catch (UnsupportedCallbackException e)
        {
            throw new LoginException("Error obtaining callback information.");
        }
        catch (IOException e)
        {
            if (_debug)
            {
                e.printStackTrace();
            }
            throw new LoginException("IO Error performing login.");
        }
        catch (Exception e)
        {
            if (_debug)
            {
                e.printStackTrace();
            }
            throw new LoginException("Error obtaining user info.");
        }
    }

    /**
     * password supplied authentication check
     *
     * @param webCredential
     * @return
     * @throws LoginException
     */
    protected boolean credentialLogin(Object webCredential) throws LoginException
    {
        setAuthenticated(getCurrentUser().checkCredential(webCredential));
        return isAuthenticated();
    }

    /**
     * binding authentication check
     * This methode of authentication works only if the user branch of the DIT (ldap tree)
     * has an ACI (acces control instruction) that allow the access to any user or at least
     * for the user that logs in.
     *
     * @param username
     * @param password
     * @return
     * @throws LoginException
     */
    protected boolean bindingLogin(String username, Object password) throws LoginException, NamingException
    {
        SearchResult searchResult = findUser(username);

        String userDn = searchResult.getNameInNamespace();

        Hashtable environment = getEnvironment();
        environment.put(Context.SECURITY_PRINCIPAL, userDn);
        environment.put(Context.SECURITY_CREDENTIALS, password);

        try
        {
            new InitialDirContext( environment );
        }
        catch (AuthenticationException e)
        {
            Log.info("Authentication failed for: " + userDn);
            throw new LoginException();
        }
        catch (NamingException ne)
        {
            throw new LoginException("Context binding failure.");
        }

        List roles = getUserRoles(username);

        UserInfo userInfo = new UserInfo(username, null, roles);

        setCurrentUser(new AbstractLoginModule.JAASUserInfo(userInfo));

        setAuthenticated(true);

        return true;
    }

    private SearchResult findUser(String username) throws NamingException, LoginException
    {
        SearchControls ctls = new SearchControls();
        ctls.setCountLimit(1);
        ctls.setDerefLinkFlag(true);
        ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String filter = "(&(objectClass=" + _userObjectClass + ")(" + _userIdAttribute + "=" + username + "))";

        Log.info("Searching for users with filter: \'" + filter + "\'" + " from base dn: " + _userBaseDn);

        NamingEnumeration results = _rootContext.search(_userBaseDn, filter, ctls);

        if (!results.hasMoreElements())
        {
            throw new LoginException("User not found.");
        }

        SearchResult searchResult = (SearchResult) results.nextElement();

        Log.info("Found user?: " + searchResult.getNameInNamespace());

        return searchResult;
    }

    /**
     * Init LoginModule.
     * Called once by JAAS after new instance is created.
     *
     * @param subject
     * @param callbackHandler
     * @param sharedState
     * @param options
     */
    public void initialize(Subject subject,
                           CallbackHandler callbackHandler,
                           Map sharedState,
                           Map options)
    {

        super.initialize(subject, callbackHandler, sharedState, options);

        _hostname = (String) options.get("hostname");
        _port = Integer.parseInt((String) options.get("port"));
        _contextFactory = (String) options.get("contextFactory");
        _bindDn = (String) options.get("bindDn");
        _bindPassword = (String) options.get("bindPassword");
        _authenticationMethod = (String) options.get("authenticationMethod");

        _userBaseDn = (String) options.get("userBaseDn");

        _roleBaseDn = (String) options.get("roleBaseDn");

        if (options.containsKey("forceBindingLogin"))
        {
            _forceBindingLogin = Boolean.parseBoolean((String) options.get("forceBindingLogin"));
        }

        _userObjectClass = (String) options.get("userObjectClass");
        _userRdnAttribute = (String) options.get("userRdnAttribute");
        _userIdAttribute = (String) options.get("userIdAttribute");
        _userPasswordAttribute = (String) options.get("userPasswordAttribute");
        _roleObjectClass = (String) options.get("roleObjectClass");
        _roleMemberAttribute = (String) options.get("roleMemberAttribute");
        _roleNameAttribute = (String) options.get("roleNameAttribute");
        _debug = Boolean.parseBoolean(String.valueOf(options.get("debug")));

        try
        {
            _rootContext = new InitialDirContext(getEnvironment());
        }
        catch (NamingException ex)
        {
            throw new IllegalStateException("Unable to establish root context", ex);
        }
    }

    /**
     * get the context for connection
     *
     * @return
     */
    public Hashtable getEnvironment()
    {
        Properties env = new Properties();

        env.put(Context.INITIAL_CONTEXT_FACTORY, _contextFactory);

        if (_hostname != null)
        {
            if (_port != 0)
            {
                env.put(Context.PROVIDER_URL, "ldap://" + _hostname + ":" + _port + "/");
            }
            else
            {
                env.put(Context.PROVIDER_URL, "ldap://" + _hostname + "/");
            }
        }

        if (_authenticationMethod != null)
        {
            env.put(Context.SECURITY_AUTHENTICATION, _authenticationMethod);
        }

        if (_bindDn != null)
        {
            env.put(Context.SECURITY_PRINCIPAL, _bindDn);
        }

        if (_bindPassword != null)
        {
            env.put(Context.SECURITY_CREDENTIALS, _bindPassword);
        }

        return env;
    }

    public static String convertCredentialJettyToLdap( String encryptedPassword )
    {
        if ("MD5:".startsWith(encryptedPassword.toUpperCase()))
        {
            return "{MD5}" + encryptedPassword.substring("MD5:".length(), encryptedPassword.length());
        }

        if ("CRYPT:".startsWith(encryptedPassword.toUpperCase()))
        {
            return "{CRYPT}" + encryptedPassword.substring("CRYPT:".length(), encryptedPassword.length());
        }

        return encryptedPassword;
    }

    public static String convertCredentialLdapToJetty( String encryptedPassword )
    {
        if (encryptedPassword == null)
        {
            return encryptedPassword;
        }

        if ("{MD5}".startsWith(encryptedPassword.toUpperCase()))
        {
            return "MD5:" + encryptedPassword.substring("{MD5}".length(), encryptedPassword.length());
        }

        if ("{CRYPT}".startsWith(encryptedPassword.toUpperCase()))
        {
            return "CRYPT:" + encryptedPassword.substring("{CRYPT}".length(), encryptedPassword.length());
        }

        return encryptedPassword;
    }

    public static byte[] digestMD5(String pwd) throws LoginException
    {
        try
        {
            MessageDigest md;

            md = MessageDigest.getInstance("MD5");
            byte[] barray = pwd.getBytes("ISO-8859-1");//todo try w/ UTF8
            for (int i = 0; i < barray.length; i++)
            {
                md.update(barray[i]);
            }

            return md.digest();
        }
        catch (UnsupportedEncodingException e)
        {
            throw new LoginException();
        }
        catch (NoSuchAlgorithmException e1)
        {
            throw new LoginException();
        }
    }
}
