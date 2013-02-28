package models;

import java.util.Date;
import java.util.List;

import com.mehteor.db.ModelUtils;
import com.mehteor.db.MongoModel;

public class Conversation extends MongoModel {
	private static int CONVERSATION_SUMMARY = 270;
	
	// ---------------------
	
	/**
	 * Conversations's title.
	 */
	private String title;
	
	/**
	 * Conversations's clean title.
	 */
	private String cleanTitle;

	/**
	 * Conversation's content.
	 */
	private String content;
	
	/**
	 * Creation's date.
	 */
	private Date creationDate;
	
	/**
	 * Last update.
	 */
	private Date lastUpdate;

	/**
	 * Creator of the project.
	 */
	// reference //
	private String creator;

	/**
	 * In which project is this todolist.
	 */
	// reference //
	private String project;

	// ---------------------
	
	public Conversation() {
		this.creationDate = new Date();
	}

	// ---------------------

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
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCleanTitle() {
		return cleanTitle;
	}

	public void setCleanTitle(String cleanTitle) {
		this.cleanTitle = cleanTitle;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getSummary() {
		if (content != null) {
			if (content.length() > CONVERSATION_SUMMARY) {
				return String.format("%s...", content.substring(0, CONVERSATION_SUMMARY)); 
			}
		}
		return content;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public List<Message> getMessages() {
		ModelUtils<Message> messages = new ModelUtils<Message>(Message.class);
		return messages.query("{'conversation': #}", this.getId());
	}
}
