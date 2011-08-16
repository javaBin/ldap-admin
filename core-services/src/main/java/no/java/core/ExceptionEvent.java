package no.java.core;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class ExceptionEvent extends Event {
    private Throwable throwable;

    protected ExceptionEvent(String shortMessage, Throwable throwable) {
        super(shortMessage, throwableToString(throwable));

        this.throwable = throwable;
    }

    private static String throwableToString(Throwable throwable) {
        CharArrayWriter buffer = new CharArrayWriter();
        throwable.printStackTrace(new PrintWriter(buffer));
        return buffer.toString();
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
