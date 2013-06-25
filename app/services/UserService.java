package services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Set;

import models.Conversation;
import models.Project;
import models.Todolist;
import models.User;
import models.UserRight;

import com.mehteor.db.ModelUtils;
import com.mehteor.qubuto.right.RightType;
import com.mehteor.qubuto.right.RightCategory;

public class UserService {
    /**
     * Creates the UserRights for the given user on the given project.
     *
     * @param   project             for which project we want to create UserRights
     * @param   user                for which user we create UserRights
     * @return the list of UserRights created.
     */
    public static List<UserRight> addCollaborator(Project project, User user) {
        List<UserRight> rights = new ArrayList<UserRight>();

        // Project

        rights.addAll(createFor(user, project));

        // Todolists
        
        for (Todolist todolist : project.getTodolists()) {
            rights.addAll(createFor(user, todolist));
        }
        
        // Conversations
        
        for (Conversation conversation : project.getConversations()) {
            rights.addAll(createFor(user, conversation));
        }

        // Saves every rights
        for (UserRight right : rights) {
            right.save();
        }

        return rights;
    }

    /**
     * Removes the UserRights for the given user on the given project.
     *
     * @param   project             for which project we want to remove UserRights
     * @param   user                for which user we remove UserRights
     * @return the number of UserRights removed.
     */
    public static int removeCollaborator(Project project, User user) {
        ModelUtils<UserRight> muRights = new ModelUtils<UserRight>(UserRight.class);
        int nbRemoved = muRights.remove("{project: #, user: #}", project.getId(), user.getId());
        return nbRemoved;
    }

    /*
    /**
     * Returns the collaborators names of a Project.
     * @param project       the project for which we want the collaborators names
     * @return the collaborators of a Project
    public static Set<String> findCollaboratorsName(Project project) {
        Set<String> users = new HashSet<String>();

        ModelUtils<UserRight> muUserRights = new ModelUtils<UserRight>(UserRight.class);
        List<UserRight> userrights = muUserRights.query("{project: #}", project.getId());

        for (UserRight right : userrights) {
            users.add(right.getUser().getUsername());
        }

        return users;
    }
    */

    /**
     * Returns the collaborators of a Project.
     * @param project       the project for which we want the collaborators ids
     * @return the collaborators of a Project
     */
    public static List<User> findCollaborators(Project project) {
        ModelUtils<UserRight> muUserRights = new ModelUtils<UserRight>(UserRight.class);
        List<UserRight> userrights = muUserRights.query("{project: #}", project.getId());

        Map<String, User> users = new HashMap<String,User>();

        for (UserRight right : userrights) {
            User user = right.getUser();
            users.put(user.getId(), user);
        }

        return new ArrayList(users.values());
    }

    /**
     * Creates and saves right for an user and a project.
     */
    public static void saveRightsFor(User user, Project project) {
        for (UserRight right : createFor(user, project)) {
            right.save();
        }
    }

    /**
     * Creates and saves right for an user and a conversation.
     */
    public static void saveRightsFor(User user, Conversation conversation) {
        for (UserRight right : createFor(user, conversation)) {
            right.save();
        }
    }
   
    /**
     * Creates and saves right for an user and a todolist.
     */
    public static void saveRightsFor(User user, Todolist todolist) {
        for (UserRight right : createFor(user, todolist)) {
            right.save();
        }
    }

    // ---------------------- 
    
    private static List<UserRight> createFor(User user, Project project) {
        List<UserRight> rights = new ArrayList<UserRight>();

        rights.add(new UserRight(user, project, RightType.READ));
        rights.add(new UserRight(user, project, RightType.UPDATE));

        rights.add(new UserRight(user, project, RightType.CREATE_TODOLIST));
        rights.add(new UserRight(user, project, RightType.CREATE_CONVERSATION));
        rights.add(new UserRight(user, project, RightType.CONFIGURE));

        return rights;
    }

    private static List<UserRight> createFor(User user, Todolist todolist) {
        List<UserRight> rights = new ArrayList<UserRight>();

        rights.add(new UserRight(user, todolist, RightType.READ));
        rights.add(new UserRight(user, todolist, RightType.UPDATE));
        rights.add(new UserRight(user, todolist, RightType.CREATE_TASK));
        rights.add(new UserRight(user, todolist, RightType.CLOSE_TASK));
        rights.add(new UserRight(user, todolist, RightType.DELETE_TASK));
        rights.add(new UserRight(user, todolist, RightType.OPEN_TASK));
        rights.add(new UserRight(user, todolist, RightType.ADD_TAG));
        rights.add(new UserRight(user, todolist, RightType.REMOVE_TAG));
        rights.add(new UserRight(user, todolist, RightType.COMMENT));

        return rights;
    }

    private static List<UserRight> createFor(User user, Conversation conversation) {
        List<UserRight> rights = new ArrayList<UserRight>();

        rights.add(new UserRight(user, conversation, RightType.READ));
        rights.add(new UserRight(user, conversation, RightType.UPDATE));
        rights.add(new UserRight(user, conversation, RightType.CREATE_MESSAGE));

        return rights;
    }
}
