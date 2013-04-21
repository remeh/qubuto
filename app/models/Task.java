package models;

import java.util.Date;
import java.util.Set;

import com.mehteor.db.ModelUtils;
import com.mehteor.db.MongoModel;

/**
 * A task in a todolist.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public class Task extends MongoModel {
	/**
	 * Author of the task.
	 */
	// reference User //
	private String author;

	/**
	 * In which todolist is this task.
	 */
	// reference //
	private String todolist;
	
	/**
	 * Content of the task.
	 */
	private String content;
	
	/**
	 * Title of the task.
	 */
	private String title;
	
	/**
	 * Creation date of the task.
	 */
	private Date creationDate;
	
	/**
	 * The close date.
	 */
	private Date closeDate;
	
	/**
	 * Tags applied to this project.
	 */
	private Set<String> tags;

	/**
	 * Position in the conversation.
	 */
	private long position;
	
	/**
	 * True if this task has been archived.
	 */
	private boolean archived;
	
	// ---------------------
	
	public Task() {
	}

	// ---------------------
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getCloseDate() {
		return closeDate;
	}

	public void setCloseDate(Date closeDate) {
		this.closeDate = closeDate;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
		this.tags = tags;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}
	
	public User getAuthor() {
		ModelUtils<User> mu = new ModelUtils<User>(User.class);
		if (author != null) {
			return mu.find(author);
		}
		return null;
	}
	
	public void setAuthor(User author) {
		this.author = author.getId();
	}
	
	public Todolist getTodolist() {
		ModelUtils<Todolist> mu = new ModelUtils<Todolist>(Todolist.class);
		if (todolist != null) {
			return mu.find(todolist);
		}
		return null;
	}
	
	public void setTodolist(Todolist todolist) {
		this.todolist = todolist.getId();
	}
}
