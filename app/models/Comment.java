package models;

import java.util.Date;

import com.mehteor.db.ModelUtils;
import com.mehteor.db.MongoModel;

/**
 * Comments on a task.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public class Comment extends MongoModel {
	/**
	 * Content of the comment.
	 */
	private String content;
	
	/**
	 * Author of the comment.
	 */
	// reference //
	private String author;
	
	/**
	 * In which task is thi comment.
	 */
	// reference //
	private String task;

	/**
	 * Creation's date.
	 */
	private Date creationDate;
	
	// ---------------------
	
	public Comment() {
	}
	
	// ---------------------
	
	public User getAuthor() {
		ModelUtils<User> mu = new ModelUtils<User>(User.class);
		if (author != null) {
			return mu.find(author);
		}
		return null;
	}
	
	public void setAuthor(User author) {
		this.author = author.id;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	public Task getTask() {
		ModelUtils<Task> mu = new ModelUtils<Task>(Task.class);
		if (task != null) {
			return mu.find(task);
		}
		return null;
	}
	
	public void setTask(Task task) {
		this.task = task.id;
	}
}
