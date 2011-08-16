package no.java.core.ldap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultLdapConnectionFactory implements InitializingBean, LdapConnectionFactory {
    public static final String DEFAULT_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

    private Hashtable<String, String> environment;

    private final static Log log = LogFactory.getLog(DefaultLdapConnectionFactory.class);

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    public DirContext getDirContext() throws NamingException {
        return new InitialDirContext(environment);
    }

    public void returnContext(DirContext context) {
        try {
            if (context == null) {
                return;
            }

            context.close();
        } catch (NamingException e) {
            // ignore
        }
    }

    public Hashtable getReadOnlyEnvironment() {
        return environment;
    }

    // -----------------------------------------------------------------------
    // Bean Lifecycle
    // -----------------------------------------------------------------------

    public void afterPropertiesSet() throws Exception {

        Map<String, String> env = new HashMap<String, String>();


        if (!StringUtils.isBlank(contextFactory)) {
            env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
        } else {
            env.put(Context.INITIAL_CONTEXT_FACTORY, DEFAULT_CONTEXT_FACTORY);
        }

        if (!StringUtils.isBlank(url)) {
            env.put(Context.PROVIDER_URL, url);
        }

        env.put(Context.SECURITY_PRINCIPAL, principal);
        env.put(Context.SECURITY_CREDENTIALS, credential);

        if (objectFactories != null && objectFactories.size() > 0) {
            String objectFactoriesString = null;
            for (Class objectFactory : objectFactories) {
                if (objectFactory == null) {
                    throw new IllegalArgumentException("Illegal argument, null object factory.");
                }

                if (objectFactoriesString == null) {
                    objectFactoriesString = objectFactory.getName();
                } else {
                    objectFactoriesString += ":" + objectFactory.getName();
                }
            }
            env.put(Context.OBJECT_FACTORIES, objectFactoriesString);
        }

        if (stateFactories != null && stateFactories.size() > 0) {
            String stateFactoriesString = null;
            for (Class stateFactory : stateFactories) {
                if (stateFactory == null) {
                    throw new IllegalArgumentException("Illegal argument, null state factory.");
                }

                if (stateFactoriesString == null) {
                    stateFactoriesString = stateFactory.getName();
                } else {
                    stateFactoriesString += ":" + stateFactory.getName();
                }
            }
            env.put(Context.STATE_FACTORIES, stateFactoriesString);
        }

        log.debug("LDAP Connection properties:");
        for (Map.Entry<String, String> entry : new TreeMap<String, String>(env).entrySet()) {
            log.debug(entry.getKey() + "=" + entry.getValue());
        }

        this.environment = new Hashtable<String, String>(Collections.unmodifiableMap(env));
    }

    // -----------------------------------------------------------------------
    // Properties
    // -----------------------------------------------------------------------

    private String contextFactory;

    public void setContextFactory(String contextFactory) {
        this.contextFactory = contextFactory;
    }

    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    private String principal;

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    private String credential;

    public void setCredential(String credential) {
        this.credential = credential;
    }

    private List<Class> objectFactories;

    public void setObjectFactories(List<Class> objectFactories) {
        this.objectFactories = objectFactories;
    }

    private List<Class> stateFactories;

    public void setStateFactories(List<Class> stateFactories) {
        this.stateFactories = stateFactories;
    }
}
