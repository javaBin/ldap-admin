package no.java.integration.cli;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Properties;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class IntegrationServer {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, org.apache.openejb.client.RemoteInitialContextFactory.class.getName());
        properties.setProperty(Context.INITIAL_CONTEXT_FACTORY, org.apache.openejb.client.LocalInitialContextFactory.class.getName());
        properties.setProperty("openejb.embedded.remotable", "true");

        // Uncomment these properties to change the defaults
        //properties.setProperty("openejb.ejbd.port", "4201");
        properties.setProperty("openejb.ejbd.bind", "0.0.0.0");
        //properties.setProperty("openejb.ejbd.threads", "200");
        //properties.setProperty("openejb.ejbd.disabled", "false");
        //properties.setProperty("openejb.ejbd.only_from", "127.0.0.1,192.168.1.1");

        properties.setProperty("openejb.telnet.port", "4202");
        properties.setProperty("openejb.telnet.name", "telnet");
        properties.setProperty("openejb.telnet.disabled", "false");
        properties.setProperty("openejb.telnet.bind", "127.0.0.1");
        properties.setProperty("openejb.telnet.threads", "5");
        properties.setProperty("openejb.telnet.server", "org.apache.openejb.server.telnet.TelnetServer");

        properties.setProperty("telnet.port", "4202");
        properties.setProperty("telnet.name", "telnet");
        properties.setProperty("telnet.disabled", "false");
        properties.setProperty("telnet.bind", "127.0.0.1");
        properties.setProperty("telnet.threads", "5");
        properties.setProperty("telnet.server", "org.apache.openejb.server.telnet.TelnetServer");

        new InitialContext(properties);

        Thread.sleep(10000000);
    }
}
