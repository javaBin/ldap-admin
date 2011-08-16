package no.java.core.ldap;

import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface LdapTemplate<T> {

    Rdn makeRdn(String rdn, Object value);

    LdapName makeDn(Rdn rdn);

    LdapName makeDn(String rdn, Object value);

    LdapName makeLdapName(String name);

    Attributes lookup(Rdn rdn);

    Attributes lookup(Rdn rdn, String... attributes);

    void unbind(Rdn rdn);

    List<SearchResult> search(String filter);

    List<SearchResult> search(String filter, Object[] arguments, String[] attributesToReturn);

    // -----------------------------------------------------------------------
    // Object
    // -----------------------------------------------------------------------

    List<T> searchObject(String filter);

    List<T> searchObject(String filter, Object[] arguments, String[] attributes);

    T lookupObject(Rdn rdn) throws NameNotFoundException;

    T lookupObject(LdapName ldapName) throws NameNotFoundException;

    void bindObject(Rdn rdn, T t);

    void rebindObject(Rdn rdn, T t);

    void modifyAttributes(Rdn rdn, ModificationItem[] modificationItems);

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    interface LdapListOperation {
        NamingEnumeration<SearchResult> perform(DirContext context) throws NamingException;
    }

    interface LdapLookupOperation {
        Object perform(DirContext context) throws NamingException;
    }

    interface LdapVoidOperation {
        void perform(DirContext context) throws NamingException;
    }
}
