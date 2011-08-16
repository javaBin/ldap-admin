package no.java.core.ldap;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.spi.DirStateFactory;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractDirStateFactory<T> implements DirStateFactory {

    private Class requiredType;

    private List<String> objectClasses = new ArrayList<String>();

    private static final String OBJECT_CLASS = "objectClass";

    private Attributes attributes;

    protected AbstractDirStateFactory(Class requiredType) {
        this.requiredType = requiredType;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    protected abstract Attributes convert(T t, Attributes attributes);

    protected void addObjectClass(String objectClass) {
        objectClasses.add(objectClass);
    }

    protected void putIfSet(String attribute, String string) {
        if(string == null) {
            return;
        }

        attributes.put(attribute, string);
    }

    protected void putIfNotEmpty(String attribute, String string) {
        if(string == null || string.trim().length() == 0) {
            return;
        }

        attributes.put(attribute, string);
    }

    // -----------------------------------------------------------------------
    // DirStateFactory Implementation
    // -----------------------------------------------------------------------

    public Object getStateToBind(Object o, Name name, Context context, Hashtable<?, ?> hashtable) throws NamingException {
        return null;
    }

    public Result getStateToBind(Object o, Name name, Context context, Hashtable<?, ?> hashtable, Attributes a) throws NamingException {
        if (o == null) {
            return null;
        }

        if (requiredType != null) {
            if (!requiredType.isAssignableFrom(o.getClass())) {
                return null;
            }
        }

        if (a != null) {
            attributes = (Attributes) a.clone();
        } else {
            attributes = new BasicAttributes();
        }

        Attribute objectClass = attributes.get(OBJECT_CLASS);
        if(objectClass != null ) {
            for (String oc : objectClasses) {
                if(!objectClass.contains(oc)) {
                    objectClass.add(oc);
                }
            }
        }
        else {
            objectClass = new BasicAttribute(OBJECT_CLASS);
            for (String oc : objectClasses) {
                objectClass.add(oc);
            }
            attributes.put(objectClass);
        }

        attributes = convert((T) o, attributes);

        if (attributes == null) {
            return null;
        }

        return new Result(null, attributes);
    }
}
