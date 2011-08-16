package no.java.core.ldap;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractDirObjectFactory implements DirObjectFactory {

    private Object object;

    private Name name;

    private Context context;

    private Hashtable<?, ?> environment;

    private Attributes attributes;

    private Set<String> requiredObjectClasses = new HashSet<String>();

    private Set<String> requiredAttributes = new HashSet<String>();

    private boolean ignoreAttributeExeptionDuringConvertion;

    private boolean warnOfUnconvertableObjects;

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    protected abstract Object convert() throws Exception;

    protected void addRequiredObjectClass(String objectClass) {
        requiredObjectClasses.add(objectClass);
    }

    protected void addRequredAttribute(String attribute) {
        requiredAttributes.add(attribute);
    }

    protected void addRequredAttribute(String[] attributes) {
        Collections.addAll(requiredAttributes, attributes);
    }

    public Object getObject() {
        return object;
    }

    public Name getName() {
        return name;
    }

    public Context getContext() {
        return context;
    }

    public Hashtable<?, ?> getEnvironment() {
        return environment;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    // -----------------------------------------------------------------------
    // Utilities for subclasses to use
    // -----------------------------------------------------------------------

    public String getStringAttribute(String name) throws NamingException {
        Attribute attribute = attributes.get(name);

        if (attribute == null) {
            throw new AttributeException("Missing required attribute '" + name + "' on object '" + this.name + "'.");
        }

        Object o = attribute.get(0);

        if (!(o instanceof String)) {
            throw new AttributeException("Attribute '" + name + "' on object '" + this.name + "' is not a string.");
        }

        return (String) o;
    }

    public String getStringAttribute(String name, String defaultValue) throws NamingException {
        Attribute attribute = attributes.get(name);

        if (attribute == null) {
            return defaultValue;
        }

        Object o = attribute.get(0);

        if (!(o instanceof String)) {
            throw new AttributeException("Attribute '" + name + "' on object '" + this.name + "' is not a string.");
        }

        return (String) o;
    }

    protected void setIgnoreAttributeExeptionDuringConvertion(boolean ignoreAttributeExeptionDuringConvertion) {
        this.ignoreAttributeExeptionDuringConvertion = ignoreAttributeExeptionDuringConvertion;
    }

    public void setWarnOfUnconvertableObjects(boolean warnOfUnconvertableObjects) {
        this.warnOfUnconvertableObjects = warnOfUnconvertableObjects;
    }

    // -----------------------------------------------------------------------
    // DirObjectFactory Implementation
    // -----------------------------------------------------------------------

    public Object getObjectInstance(Object object, Name name, Context context, Hashtable<?, ?> environment) throws Exception {
        return null;
    }

    public Object getObjectInstance(Object object, Name name, Context context, Hashtable<?, ?> environment, Attributes attributes) throws Exception {
        this.object = object;
        this.name = name;
        this.context = context;
        this.environment = environment;
        this.attributes = attributes;

        try {
            Object o = convert(attributes);

            if (o == null) {
                if (warnOfUnconvertableObjects) {
                    System.err.println("Converter '" + getClass().getName() + "' could not convert " + name.toString());
                }
                return null;
            } else {
                return o;
            }
        } catch (AttributeException e) {
            if (ignoreAttributeExeptionDuringConvertion) {
                e.printStackTrace();
                return null;
            }

            throw e;
        }
    }

    // -----------------------------------------------------------------------
    // private
    // -----------------------------------------------------------------------

    private Object convert(Attributes attributes) throws Exception {
        if (requiredObjectClasses.size() > 0) {
            if (attributes == null) {
                return null;
            }

            Attribute objectClass = attributes.get("objectClass");

            if (objectClass == null) {
                return null;
            }

            for (String requiredObjectClass : requiredObjectClasses) {
                if (!objectClass.contains(requiredObjectClass)) {
                    return null;
                }
            }
        }

        if (requiredAttributes.size() > 0) {
            if (attributes == null) {
                return null;
            }

            for (String requiredAttribute : requiredAttributes) {
                if (attributes.get(requiredAttribute) == null) {
                    return null;
                }
            }
        }

        return convert();
    }
}
