package models;


import java.util.Date;

import play.libs.Json;

import models.User;
import models.Conversation;
import models.Project;
import models.Todolist;

import com.mehteor.qubuto.right.RightCategory;
import com.mehteor.qubuto.right.RightType;
import com.mehteor.db.MongoModel;
import com.mehteor.db.ModelUtils;

/**
 * Right for an User of Qubuto.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public class UserRight extends MongoModel {
	/**
	 * User's username.
	 */
	private String user;

    /**
     * The RightCategory. Ex: PROJECT, TODOLIST, ...
     */
    private String category;

    /**
     * The project on which impacts this right.
     */
    private String project;

    /**
     * The objectId of this Right.
     *
     * Represents for exemple the id of a Todolist.
     * <b>Can be null</b>
     */
    private String objectId;

    /**
     * The RightType. Ex: READ, CREATE_TASK, ...
     */
    private String type;

	/**
	 * Creation date.
	 */
	private Date creationDate;
	
	// ---------------------
	
	public UserRight() {
        this.creationDate = new Date();
	}

    public UserRight(String right) {
        this();
        init(right);
    }

    public UserRight(User user, RightCategory category, Project project, String objectId, RightType type) {
        this();
        this.user       = user.getId();
        this.category   = category.toString();
        this.project    = project.getId();
        this.objectId   = objectId;
        this.type       = type.toString();
    }

    public UserRight(User user, Project project, RightType type) {
        this();
        this.user       = user.getId();
        this.category   = RightCategory.PROJECT.toString();
        this.project    = project.getId();
        this.objectId   = project.getId();
        this.type       = type.toString();
    }

    public UserRight(User user, Conversation conversation, RightType type) {
        this();
        this.user       = user.getId();
        this.category   = RightCategory.CONVERSATION.toString();
        this.project    = conversation.getProject().getId();
        this.objectId   = conversation.getId();
        this.type       = type.toString();
    }

    public UserRight(User user, Todolist todolist, RightType type) {
        this();
        this.user       = user.getId();
        this.category   = RightCategory.TODOLIST.toString();
        this.project    = todolist.getProject().getId();
        this.objectId   = todolist.getId();
        this.type       = type.toString();
    }

    // ---------------------- 

    private void init(String right) {
        if (right == null || right.isEmpty()) {
            throw new IllegalArgumentException("The string description can't be null or empty.");
        }

        String[] parts = right.split(":"); 
        if (parts.length != 4) {
            throw new IllegalArgumentException("Bad format for the string description. Must be \"Category:projectId:id:Type\"");
        }

        this.category = parts[0];

        this.project = parts[1];

        if (parts[2].isEmpty()) {
            this.objectId = null;
        } else {
            this.objectId = parts[2];
        }

        this.type = parts[3];
    }

    // ---------------------- 

    /**
     * Inits this right from the given string description.
     * @param   right       the string description to read.
     */
    public static UserRight fromString(String right) {
        return new UserRight(right);
    }

	// ---------------------

	public Project getProject() {
		ModelUtils<Project> mu = new ModelUtils<Project>(Project.class);
		if (project != null) {
			return mu.find(project);
		}
		return null;
	}

    public void setProject(Project project) {
        this.project = project.getId();
    }

	public User getUser() {
		ModelUtils<User> mu = new ModelUtils<User>(User.class);
		if (user != null) {
			return mu.find(user);
		}
		return null;
	}

	public void setUser(User user) {
		this.user = user.getId();
	}

    public RightCategory getCategory() {
        return RightCategory.valueOf(category);
    }

    public void setCategory(RightCategory category) {
        this.category = category.toString();
    }

    public RightType getType() {
        return RightType.valueOf(type);
    }

    public void setType(RightType type) {
        this.type = type.toString();
    }

	public Date getCreationDate() {
		return creationDate;
	}
	
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

    // ---------------------- 

    public Conversation getConversation() {
        if (objectId == null) {
            return null;
        }
		ModelUtils<Conversation> mu = new ModelUtils<Conversation>(Conversation.class);
        return mu.find(objectId);
    }

    public Todolist getTodolist() {
        if (objectId == null) {
            return null;
        }
		ModelUtils<Todolist> mu = new ModelUtils<Todolist>(Todolist.class);
        return mu.find(objectId);
    }

    // ---------------------- 

    @Override
    public String toString() {
        return String.format("%s:%s:%s", this.category, (this.objectId == null ? "" : this.objectId), this.type);
    }
}

