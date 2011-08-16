package no.java.integration.service;

import javax.annotation.Resource;
import javax.ejb.Init;
import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import java.security.Principal;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
@Remote
@Stateless
public class DefaultUserAdminService implements UserAdminService {

    @Resource
    private SessionContext context;

    // -----------------------------------------------------------------------
    // UserAdminService Implementation
    // -----------------------------------------------------------------------

    public void createUser() {
        System.out.println("DefaultUserAdminService.createUser");

        Principal principal = context.getCallerPrincipal();

        System.out.println("principal = " + principal);
        System.out.println("principal.getName() = " + principal.getName());
    }

    // -----------------------------------------------------------------------
    //
    // -----------------------------------------------------------------------

    @Init
    public void init() {
        System.out.println("true = " + true);
    }
}
