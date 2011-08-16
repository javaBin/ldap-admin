package no.java.core;

import org.springframework.beans.factory.InitializingBean;

import java.util.Random;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultPasswordGenerator implements PasswordGenerator, InitializingBean {

    private Random random;

    private char[] alphabet = {
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '!', '"', '#', '%', '&', '/', '(', ')', '=', '?',
    };

    // -----------------------------------------------------------------------
    // PasswordGenerator Implementation
    // -----------------------------------------------------------------------

    public String generatePassword() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < length; i++) {
            buffer.append(alphabet[random.nextInt(alphabet.length)]);
        }
        return buffer.toString();
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    private int length;

    public void setLength(int length) {
        this.length = length;
    }

    public void afterPropertiesSet() throws Exception {
        random = new Random(System.currentTimeMillis());
    }
}
