package models;

import java.util.Date;
import java.util.List;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

import models.QubutoModel;

import com.mehteor.db.ModelUtils;

/**
 * Qubuto projects.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public class Project extends QubutoModel
{
	/**
	 * Project's name.
	 */
	private String name;

	/**
	 * Project's clean name.
	 */
	private String cleanName;
	
	/**
	 * Project's description.
	 */
	private String description;
	
	/**
	 * Creation's date.
	 */
	private Date creationDate;

	/**
	 * Creator of the project.
	 */
	// reference //
	private String creator;
	
//	/**
//	 * This project todo lists
//	 */
//	private Set<String> todolists;
//	@JsonIgnore
	
	// ---------------------
	
	public Project() {
		this.creationDate = new Date();
	}

	// ---------------------

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCleanName() {
		return cleanName;
	}

	public void setCleanName(String cleanName) {
		this.cleanName = cleanName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

    @Override
	public User getCreator() {
        Object cache = cache("creator");
        if (cache != null) {
            return (User)cache;
        }

		ModelUtils<User> mu = new ModelUtils<User>(User.class);
		if (creator != null) {
			return (User) cache("creator", mu.find(creator));
		}
		return null;
	}

	public void setCreator(User creator) {
        invalidate("creator");
		this.creator = creator.getId();
	}
	
	public List<Todolist> getTodolists() {
        Object cache = cache("todolists");
        if (cache != null) {
            return (List<Todolist>)cache;
        }

		ModelUtils<Todolist> todolists = new ModelUtils<Todolist>(Todolist.class);
		return (List<Todolist>) cache("todolists", todolists.query("{'project': #}", this.getId()));
	}
	
	public List<Conversation> getConversations() {
        Object cache = cache("conversations");
        if (cache != null) {
            return (List<Conversation>)cache;
        }

		ModelUtils<Conversation> conversations = new ModelUtils<Conversation>(Conversation.class);
		return (List<Conversation>) cache("conversations", conversations.query("{'project': #}", this.getId()));
	}

	// ---------------------
	
	/**
	 * Returns a Project jsoned for the view.
	 * @return ObjectNode the Task jsonsed for Action diffusion.
	 */
	public ObjectNode toJsonView() {
		ObjectNode node = Json.newObject();
        node.put("creator",         getCreator().getUsername());
        node.put("cleanName",       getCleanName());
		return node;
	}

	/**
	 * Returns a Project jsoned for Action diffusion.
	 * @return ObjectNode the Task jsonsed for Action diffusion.
	 */
	public ObjectNode toJsonAction() {
        return toJsonView();
	}
    
}
