package no.java.core.ldap;

import org.springframework.beans.factory.InitializingBean;

import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultLdapTemplate<T> implements LdapTemplate<T>, InitializingBean {

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    private LdapName baseDn;

    // -----------------------------------------------------------------------
    // LdapTemplate Implementation
    // -----------------------------------------------------------------------

    public Rdn makeRdn(String rdn, Object value) {
        if (rdn == null) {
            throw new NullPointerException("rdn is null");
        }
        if (value == null) {
            throw new NullPointerException("value is null");
        }

        try {
            return new Rdn(rdn, value);
        } catch (InvalidNameException e) {
            throw new RuntimeException("Error while creating Rdn of rdn '" + rdn + "', value: '" + value + "'.", e);
        }
    }

    public LdapName makeDn(Rdn rdn) {
        if (rdn == null) {
            throw new NullPointerException("rdn is null");
        }

        LdapName name = (LdapName) baseDn.clone();

        name.add(rdn);

        return name;
    }

    public LdapName makeDn(String rdn, Object value) {
        if (rdn == null) {
            throw new NullPointerException("rdn is null");
        }
        if (value == null) {
            throw new NullPointerException("value is null");
        }

        try {
            LdapName name = (LdapName) baseDn.clone();

            name.add(new Rdn(rdn, value));

            return name;
        } catch (InvalidNameException e) {
            throw new RuntimeException("Error while creating Rdn of rdn '" + rdn + "', value: '" + value + "'.", e);
        }
    }

    public LdapName makeLdapName(String name) {
        try {
            return new LdapName(name);
        } catch (InvalidNameException e) {
            throw new RuntimeException(e);
        }
    }

    public Attributes lookup(Rdn rdn) {
        return lookup(rdn, (String[]) null);
    }

    public Attributes lookup(final Rdn rdn, final String... attributes) {
        List<SearchResult> list = searchInternal(baseDnPlusRdn(rdn), null, null, attributes);
        if (list.size() == 0) {
            return null;
        }

        SearchResult searchResult = list.get(0);

        return searchResult.getAttributes();
    }

    public void unbind(final Rdn rdn) {
        execute(new LdapVoidOperation() {
            public void perform(DirContext context) throws NamingException {
                context.unbind(baseDnPlusRdn(rdn));
            }
        });
    }

    public List<SearchResult> search(String filter) {
        return search(filter, null, (String[]) null);
    }

    public List<SearchResult> search(String filter, Object[] arguments, String... attributesToReturn) {
        return searchInternal(baseDn, filter, arguments, attributesToReturn);
    }

    public List<T> searchObject(String filter) {
        return searchObject(filter, null, (String[]) null);
    }

    public List<T> searchObject(final String filter, Object[] arguments, final String... attributesToReturn) {
        return execute(new SuperLdapListOperation(baseDn, filter, arguments, attributesToReturn), true);
    }

    public T lookupObject(final Rdn rdn) throws NameNotFoundException {
        return lookupObject(baseDnPlusRdn(rdn));
    }

    public T lookupObject(final LdapName ldapName) throws NameNotFoundException {
        LdapLookupOperation operation = new LdapLookupOperation() {
            public Object perform(DirContext context) throws NamingException {
                return context.lookup(ldapName);
            }
        };

        return (T) execute(operation, true);
    }

    public void bindObject(final Rdn rdn, final T t) {
        LdapVoidOperation operation = new LdapVoidOperation() {
            public void perform(DirContext context) throws NamingException {
                context.bind(baseDnPlusRdn(rdn), t);
            }
        };

        execute(operation);
    }

    public void rebindObject(final Rdn rdn, final T t) {
        LdapVoidOperation operation = new LdapVoidOperation() {
            public void perform(DirContext context) throws NamingException {
                context.rebind(baseDnPlusRdn(rdn), t);
            }
        };

        execute(operation);
    }

    public void modifyAttributes(final Rdn rdn, final ModificationItem[] modificationItems) {
        LdapVoidOperation operation = new LdapVoidOperation() {
            public void perform(DirContext context) throws NamingException {
                LdapName name = baseDnPlusRdn(rdn);
                context.modifyAttributes(name, modificationItems);
            }
        };

        execute(operation);
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    private LdapName baseDnPlusRdn(Rdn rdn) {
        LdapName name = (LdapName) baseDn.clone();
        name.add(rdn);
        return name;
    }

    private List<SearchResult> searchInternal(final LdapName name, final String filter, final Object[] arguments,
                                              final String[] attributesToReturn) {
        return execute(new SuperLdapListOperation(name, filter, arguments, attributesToReturn), false);
    }

    private Object execute(LdapLookupOperation operation, boolean returnConvertedObject) throws NameNotFoundException {
        DirContext context = null;

        try {
            context = connectionFactory.getDirContext();

            Object o = operation.perform(context);

            if (o == null) {
                return null;
            }

            if (returnConvertedObject) {
                if (targetClass.isAssignableFrom(o.getClass())) {
                    return o;
                }

                throw new NameNotFoundException("Object was found but not of target type. Was: " + o.getClass());
            }

            return o;
        }
        catch (NameNotFoundException e) {
            throw e;
        } catch (NamingException e) {
            throw new RuntimeException("Error while performing operation.", e);
        }
        finally {
            connectionFactory.returnContext(context);
        }
    }

    private void execute(LdapVoidOperation operation) {
        DirContext context = null;

        try {
            context = connectionFactory.getDirContext();

            operation.perform(context);
        } catch (NamingException e) {
            throw new RuntimeException("Error while performing operation.", e);
        }
        finally {
            connectionFactory.returnContext(context);
        }
    }

    private List execute(LdapListOperation operation, boolean returnConvertedObject) {
        DirContext context = null;

        try {
            context = connectionFactory.getDirContext();

            NamingEnumeration<SearchResult> namingEnumeration = operation.perform(context);

            List<Object> result = new ArrayList<Object>();

            while (namingEnumeration.hasMore()) {
                SearchResult searchResult = namingEnumeration.next();

                if (returnConvertedObject) {
                    Object o = searchResult.getObject();

                    // when converting all the returned attribute sets to objects, ignore the ones that didn't get
                    // converted. This should probably a customizable option, but this would be the default behaviour.
                    if (o instanceof Context) {
                    } else {
                        if (targetClass.isAssignableFrom(o.getClass())) {
                            result.add(o);
                        }
                    }
                } else {
                    result.add(searchResult);
                }
            }

            return result;
        } catch (NamingException e) {
            throw new RuntimeException("Error while performing operation.", e);
        }
        finally {
            connectionFactory.returnContext(context);
        }
    }

    // -----------------------------------------------------------------------
    // InitializingBean Implementatino
    // -----------------------------------------------------------------------

    public void afterPropertiesSet() throws Exception {
        if (baseDnString == null) {
            throw new IllegalArgumentException("Missing required property: baseDn");
        }

        if (targetClass == null) {
            throw new IllegalArgumentException("Missing required property: targetClass");
        }

        baseDn = new LdapName(baseDnString);
    }

    // -----------------------------------------------------------------------
    // Properties
    // -----------------------------------------------------------------------

    private String baseDnString;

    public void setBaseDn(String baseDn) {
        this.baseDnString = baseDn;
    }

    private LdapConnectionFactory connectionFactory;

    public void setConnectionFactory(LdapConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    private Class targetClass;

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    private static class SuperLdapListOperation implements LdapListOperation {
        private Name name;
        private String filter;
        private Object[] arguments;
        private String[] attributesToReturn;

        public SuperLdapListOperation(Name name, String filter, Object[] arguments, String[] attributesToReturn) {
            this.name = name;
            this.filter = filter;
            this.arguments = arguments;
            this.attributesToReturn = attributesToReturn;
        }

        public NamingEnumeration<SearchResult> perform(DirContext context) throws NamingException {
            SearchControls searchControls = new SearchControls();

            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            searchControls.setReturningObjFlag(true);
            searchControls.setReturningAttributes(attributesToReturn);

            String f = filter == null ? "objectClass=*" : filter;

            return context.search(name, f, arguments, searchControls);
        }
    }
}
