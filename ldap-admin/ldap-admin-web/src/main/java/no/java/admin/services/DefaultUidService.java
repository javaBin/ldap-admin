package no.java.admin.services;

import org.springframework.beans.factory.InitializingBean;

import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultUidService implements UidService, InitializingBean {

    private Map<Character, String> characterMap;
    private Set<Character> validCharacters;

    // -----------------------------------------------------------------------
    // UidService Implementaion
    // -----------------------------------------------------------------------

    public String generateUid(String firstName, String lastName) {
        if (firstName == null || lastName == null) {
            return null;
        }

        firstName = firstName.trim();
        lastName = lastName.trim();

        if (firstName.length() == 0 || lastName.length() == 0) {
            return null;
        }

        firstName = firstName.toLowerCase();
        lastName = lastName.toLowerCase();

        if(firstName.endsWith(".")) {
            firstName = firstName.substring(0, firstName.length() - 1);
        }

        if(lastName.endsWith(".")) {
            lastName = lastName.substring(0, lastName.length() - 1);
        }

        StringBuffer buffer = new StringBuffer();

        convert(buffer, firstName);
        buffer.append('.');
        convert(buffer, lastName);

        String uid = buffer.toString();

        return isValidUid(uid) ? uid : null;
    }

    public boolean isValidUid(String uid) {
        for (char c : uid.toCharArray()) {
            if (!validCharacters.contains(c)) {
                return false;
            }
        }

        return true;
    }

    // -----------------------------------------------------------------------
    // Private
    // -----------------------------------------------------------------------

    public void convert(StringBuffer buffer, String string) {
        char[] chars = string.toCharArray();

        for (char c : chars) {
            String replacement = characterMap.get(c);

            if (replacement == null) {
                buffer.append(c);
            } else {
                buffer.append(replacement);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Spring Properties
    // -----------------------------------------------------------------------

    public void afterPropertiesSet() throws Exception {
        // For some annoying reason spring can't do this with xml:
        characterMap.put(' ', ".");
    }

    public void setCharacterMap(Map<Character, String> characterMap) {
        this.characterMap = characterMap;
    }

    public void setValidCharacters(Set<Character> validCharacters) {
        this.validCharacters = validCharacters;
    }
}
