package no.java.core;

import no.java.core.model.Group;

import java.util.List;

/**
 * @author <a href="mailto:trygvis@java.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface GroupService {

    List<String> getAvailableGroups(String uid);

    List<Group> getGroups();

    Group getGroup(String cn);

    void saveGroup(Group group);
}
