package no.java.security.test;

import java.security.Permission;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class GenericServicePermission extends Permission {

    private String action;

    public GenericServicePermission(String name, String action) throws IllegalAccessException {
        super(name);

        if(action == null) {
            throw new IllegalAccessException("Invalid action: " + action);
        }
        this.action = action;
    }

    public boolean implies(Permission p) {
        System.out.println("GenericServicePermission.implies");
        System.out.println("permission = " + p);
        if (!(p instanceof GenericServicePermission))
            return false;

        GenericServicePermission permission = (GenericServicePermission) p;

//        boolean implied = actions.containsAll(permission.actions);
        boolean implied = getName().equals(permission.getName());

        System.out.println("implied = " + implied);

        return implied;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenericServicePermission)) return false;

        GenericServicePermission that = (GenericServicePermission) o;

        return action.equals(that.action);
    }

    public int hashCode() {
        return action.hashCode();
    }

    public String getActions() {
//        StringBuffer stringBuffer = new StringBuffer();
//
//        for (String action : actions) {
//            if (stringBuffer.length() == 0) {
//                stringBuffer.append(action);
//            } else {
//                stringBuffer.append(",");
//                stringBuffer.append(action);
//            }
//        }
//
//        return stringBuffer.toString();

        return action;
    }
}
