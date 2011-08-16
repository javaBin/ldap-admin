package no.java.core;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class Log4jEventMonitor implements EventMonitor, InitializingBean {

    private Logger monitorLog;

    private String category;

    // -----------------------------------------------------------------------
    // EventMonitor Implementation
    // -----------------------------------------------------------------------

    public void dispatchEvent(Event event) {
        // TODO: Add pluggable support for event formatters

        if (event instanceof ExceptionEvent) {
            ExceptionEvent exceptionEvent = (ExceptionEvent) event;
            monitorLog.info(event.getShortMessage(), exceptionEvent.getThrowable());
        } else {
            monitorLog.info(event.getClass().getName() + ":" + event.getShortMessage() + ":" + event.getLongMessage());
        }
    }

    // -----------------------------------------------------------------------
    // Spring
    // -----------------------------------------------------------------------

    public void setCategory(String category) {
        this.category = category;
    }

    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isEmpty(category)) {
            category = "MONITOR";
        }

        monitorLog = Logger.getLogger(category);
    }
}
