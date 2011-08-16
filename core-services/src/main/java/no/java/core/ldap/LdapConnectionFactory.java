package no.java.core.ldap;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import java.util.Hashtable;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface LdapConnectionFactory {
    DirContext getDirContext() throws NamingException;

    void returnContext(DirContext context);

    Hashtable getReadOnlyEnvironment();
}
