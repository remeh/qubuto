package models;

import java.util.Date;
import java.util.List;

import com.mehteor.db.ModelUtils;
import com.mehteor.db.MongoModel;

/**
 * Qubuto projects.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public class Project extends MongoModel
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

	public User getCreator() {
		ModelUtils<User> mu = new ModelUtils<User>(User.class);
		if (creator != null) {
			return mu.find(creator);
		}
		return null;
	}

	public void setCreator(User creator) {
		this.creator = creator.getId();
	}
	
//	public void addTodolist(Todolist todolist)
//	{
//		todolists = ruTodolist.add(todolists, todolist);
//	}
//	
//	public void removeTodolist(Todolist todolist)
//	{
//		todolists = ruTodolist.remove(todolists, todolist);
//	}
	
	public List<Todolist> getTodolists() {
		ModelUtils<Todolist> todolists = new ModelUtils<Todolist>(Todolist.class);
		return todolists.query("{'project': #}", this.getId());
	}
	
	public List<Conversation> getConversations() {
		ModelUtils<Conversation> conversations = new ModelUtils<Conversation>(Conversation.class);
		return conversations.query("{'project': #}", this.getId());
	}
}
