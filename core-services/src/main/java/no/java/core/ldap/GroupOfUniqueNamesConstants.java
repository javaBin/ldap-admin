package no.java.core.ldap;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public final class GroupOfUniqueNamesConstants {
    public static final String NAME = "groupOfUniqueNames";

    // MUST
    public static final String UNIQUE_MEMBER = "uniqueMember";
    public static final String CN = "cn";

    // MAY
    public static final String BUSINESS_CATEGORY = "businessCategory";
    public static final String SEE_ALSO = "seeAlso";
    public static final String OWNER = "owner";
    public static final String OU = "ou";
    public static final String O = "o";
    public static final String DESCRIPTION = "description";
}
