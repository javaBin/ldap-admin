package no.java.admin.web;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.BlockingChannelConnector;
import org.mortbay.jetty.plus.jaas.JAASUserRealm;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.log.Log;

import java.io.File;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class LdapAdminWebEmbedder {
    private File basedir;

    private Server server;
    private int port;

    public LdapAdminWebEmbedder(File basedir, int port) {
        this.basedir = basedir;
        this.port = port;
    }

    public void start() throws Exception {
        if (System.getProperty("basedir") == null) {
            System.setProperty("basedir", basedir.getAbsolutePath());
        }
        File jaasConfig = new File(basedir, "src/test/resources/jaas.config");

        if (!jaasConfig.canRead()) {
            throw new Exception("Unable to read JAAS configuration file: " + jaasConfig.getAbsolutePath() + ".");
        }

        System.setProperty("java.security.auth.login.config", jaasConfig.getAbsolutePath());

        Log.getLog().setDebugEnabled(false);

        Server server = new Server();
        BlockingChannelConnector connector = new BlockingChannelConnector();
        if (port > 0) {
            connector.setPort(port);
        }
        server.addConnector(connector);

//        if (!(LdapAdminWebEmbedder.class.getClassLoader() instanceof URLClassLoader)) {
//            throw new RuntimeException("bah");
//        }
//
//        URLClassLoader urlClassLoader = (URLClassLoader) LdapAdminWebEmbedder.class.getClassLoader();
//
//        List<URL> urls = new ArrayList<URL>();
//        urls.add(new File(basedir, "src/test/resources").toURI().toURL());
//        for (URL url : urlClassLoader.getURLs()) {
//            String s = url.toExternalForm();
//            if (!s.contains("/test-classes/")) {
//                urls.add(url);
//            }
//            else {
//                System.out.println("Skipping " + url);
//            }
//        }

        WebAppContext webapp = new WebAppContext();
//        webapp.setClassLoader(new URLClassLoader(urls.toArray(new URL[urls.size()])));
        webapp.setExtraClasspath(new File(basedir, "src/test/resources").getAbsolutePath());
        webapp.setExtractWAR(false);
        webapp.setContextPath("/ldap-admin");
        webapp.setWar(new File(basedir, "src/main/webapp").getAbsolutePath());
//        String s = new File(basedir, "src/main/resources").toURI().toURL().toExternalForm() + ";";
//        s += new File(basedir, "target/classes").toURI().toURL().toExternalForm();
//        webapp.setExtraClasspath(s);
        server.addHandler(webapp);

        JAASUserRealm realm = new JAASUserRealm();
        realm.setName("javabin realm");
        realm.setLoginModuleName("ldap-admin");
        realm.setCallbackHandlerClass("org.mortbay.jetty.plus.jaas.callback.DefaultCallbackHandler");
//        realm.setRoleClassNames(new String[] {"org.apache.geronimo.security.realm.providers.GeronimoGroupPrincipal"});
        server.addUserRealm(realm);

        server.start();

        this.port = connector.getLocalPort();
        this.server = server;
    }

    public void stop() throws Exception {
        if (server == null) {
            return;
        }

        server.stop();
    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) throws Exception {
        LdapAdminWebEmbedder embedder = new LdapAdminWebEmbedder(new File("."), 10000);

        try {
            embedder.start();

            Thread.sleep(100000000000L);

            System.out.println("embedder.getPort() = " + embedder.getPort());
        } finally {
            embedder.stop();
        }
    }
}
