package no.java.admin.services;

import no.java.core.TechnicalException;
import no.java.core.model.User;

import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface UserCreationRequestService {
    String storeRequest(User user) throws TechnicalException;

    List<UserCreationRequest> getRequests() throws TechnicalException;

    UserCreationRequest getRequest(String requestId) throws TechnicalException;

    void updateRequest(UserCreationRequest request) throws TechnicalException;

    UserCreationResult approveRequest(String requestId) throws TechnicalException;

    void rejectRequest(String requestId) throws TechnicalException;
}
