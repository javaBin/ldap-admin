package no.java.admin.services;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import no.java.core.TechnicalException;
import no.java.core.UserService;
import no.java.core.model.User;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultUserCreationRequestService implements UserCreationRequestService, InitializingBean {

    private final static Logger log = Logger.getLogger(DefaultUserCreationRequestService.class);

    private String encoding = "UTF-8";

    private Charset charset;

    private XStream xstream;

    private File basedir;

    private UserService userService;

    // -----------------------------------------------------------------------
    // UserCreationRequestService Implementation
    // -----------------------------------------------------------------------

    public String storeRequest(User user) throws TechnicalException {
        String requestId = String.valueOf(System.currentTimeMillis());

        // -----------------------------------------------------------------------
        // Assemble the request
        // -----------------------------------------------------------------------

        UserCreationRequest request = new UserCreationRequest();

        request.setUser(user);

        request.setRequestId(requestId);

        updateRequest(request);

        return requestId;
    }

    public List<UserCreationRequest> getRequests() throws TechnicalException {
        File[] files = basedir.listFiles(new FilenameFilter() {
            public boolean accept(File file, String s) {
                return s.endsWith(".xml");
            }
        });

        if (files == null) {
            throw new TechnicalException("Error while loading files from " + basedir.getAbsolutePath());
        }

        List<UserCreationRequest> userCreationRequests = new ArrayList<UserCreationRequest>(files.length);
        for (File file : files) {
            userCreationRequests.add(loadRequest(file));
        }

        return userCreationRequests;
    }

    public UserCreationRequest getRequest(String requestId) throws TechnicalException {
        return loadRequest(makeFile(requestId));
    }

    public void updateRequest(UserCreationRequest request) throws TechnicalException {
        File file = makeFile(request.getRequestId());

        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), charset);

            writer.write("<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>");

            xstream.toXML(request, writer);
        } catch (FileNotFoundException e) {
            throw new TechnicalException("Could not write to file: '" + file.getAbsolutePath() + "'.");
        } catch (IOException e) {
            throw new TechnicalException("");
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public UserCreationResult approveRequest(String requestId) throws TechnicalException {
        File file = makeFile(requestId);

        if (!file.canRead() || !file.canWrite()) {
            throw new TechnicalException("Can't read and/or write file: " + file.getAbsolutePath());
        }

        UserCreationRequest request = loadRequest(file);

        User user = userService.createUser(request.getUser());

        // delete the request
        if (!file.delete()) {
            throw new TechnicalException("Unable to delete request file: '" + file.getAbsolutePath() + "' but the user was created.");
        }

        UserCreationResult result = new UserCreationResult();
        result.setUser(user);
        return result;
    }

    public void rejectRequest(String requestId) throws TechnicalException {
        File file = makeFile(requestId);

        if (!file.exists()) {
            throw new TechnicalException("No such request: " + requestId + ".");
        }

        if (!file.delete()) {
            throw new TechnicalException("Error while deleting file: " + file.getAbsolutePath());
        }
    }

    // -----------------------------------------------------------------------
    // Bean Lifecycle
    // -----------------------------------------------------------------------

    public void afterPropertiesSet() throws Exception {
        if (basedir == null) {
            throw new IllegalArgumentException("Missing required property: basedir");
        }

        basedir = basedir.getAbsoluteFile();

        log.info("Storing user requests in " + basedir + ".");

        if(basedir.exists()) {
            if(!basedir.isDirectory()) {
                throw new IllegalArgumentException("basedir exist and is not a directory: '" + basedir.getAbsolutePath() + "'");
            }
        }
        else {
            if(!basedir.mkdirs()) {
                throw new IllegalArgumentException("Unable to create directory: '" + basedir.getAbsolutePath() + "'");
            }
        }

        charset = Charset.forName(encoding);

        xstream = new XStream(new DomDriver(encoding));
        xstream.alias("user-creation-request", UserCreationRequest.class);
    }

    public String getBasedir() {
        return basedir.getAbsolutePath();
    }

    public void setBasedir(String basedir) {
        this.basedir = new File(basedir);
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    private UserCreationRequest loadRequest(File file) throws TechnicalException {
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(file), charset);
            return (UserCreationRequest) xstream.fromXML(reader);
        } catch (FileNotFoundException e) {
            throw new TechnicalException("Could not find file: '" + file.getAbsolutePath() + "'.");
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    private File makeFile(String requestId) {
        if (requestId == null || StringUtils.isEmpty(requestId)) {
            throw new TechnicalException("Illegal request id: " + requestId);
        }

        return new File(basedir, requestId + ".xml");
    }
}
