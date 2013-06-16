package services;

import java.util.List;
import java.util.ArrayList;

import models.Conversation;
import models.Project;
import models.Todolist;
import models.User;
import models.UserRight;

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
   
    // ---------------------- 
    
    private static List<UserRight> createFor(User user, Project project) {
        List<UserRight> rights = new ArrayList<UserRight>();

        rights.add(new UserRight(user.getId(), RightCategory.PROJECT.toString(), project.getId(), RightType.READ.toString()));
        rights.add(new UserRight(user.getId(), RightCategory.PROJECT.toString(), project.getId(), RightType.UPDATE.toString()));

        rights.add(new UserRight(user.getId(), RightCategory.PROJECT.toString(), project.getId(), RightType.CREATE_TODOLIST.toString()));
        rights.add(new UserRight(user.getId(), RightCategory.PROJECT.toString(), project.getId(), RightType.CREATE_CONVERSATION.toString()));
        rights.add(new UserRight(user.getId(), RightCategory.PROJECT.toString(), project.getId(), RightType.CONFIGURE.toString()));

        return rights;
    }

    private static List<UserRight> createFor(User user, Todolist todolist) {
        List<UserRight> rights = new ArrayList<UserRight>();

        rights.add(new UserRight(user.getId(), RightCategory.TODOLIST.toString(), todolist.getId(), RightType.READ.toString()));
        rights.add(new UserRight(user.getId(), RightCategory.TODOLIST.toString(), todolist.getId(), RightType.UPDATE.toString()));
        rights.add(new UserRight(user.getId(), RightCategory.TODOLIST.toString(), todolist.getId(), RightType.CREATE_TASK.toString()));
        rights.add(new UserRight(user.getId(), RightCategory.TODOLIST.toString(), todolist.getId(), RightType.CLOSE_TASK.toString()));
        rights.add(new UserRight(user.getId(), RightCategory.TODOLIST.toString(), todolist.getId(), RightType.DELETE_TASK.toString()));
        rights.add(new UserRight(user.getId(), RightCategory.TODOLIST.toString(), todolist.getId(), RightType.OPEN_TASK.toString()));
        rights.add(new UserRight(user.getId(), RightCategory.TODOLIST.toString(), todolist.getId(), RightType.ADD_TAG.toString()));
        rights.add(new UserRight(user.getId(), RightCategory.TODOLIST.toString(), todolist.getId(), RightType.REMOVE_TAG.toString()));
        rights.add(new UserRight(user.getId(), RightCategory.TODOLIST.toString(), todolist.getId(), RightType.COMMENT.toString()));

        return rights;
    }

    private static List<UserRight> createFor(User user, Conversation conversation) {
        List<UserRight> rights = new ArrayList<UserRight>();

        rights.add(new UserRight(user.getId(), RightCategory.CONVERSATION.toString(), conversation.getId(), RightType.READ.toString()));
        rights.add(new UserRight(user.getId(), RightCategory.CONVERSATION.toString(), conversation.getId(), RightType.UPDATE.toString()));
        rights.add(new UserRight(user.getId(), RightCategory.CONVERSATION.toString(), conversation.getId(), RightType.CREATE_MESSAGE.toString()));

        return rights;
    }
}
