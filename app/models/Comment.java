package models;

import java.util.Date;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

import controllers.Application;

import models.QubutoModel;

import com.mehteor.db.ModelUtils;

/**
 * Comments on a task.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public class Comment extends QubutoModel {
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
	
    public User getCreator() {
        return getAuthor();
    }

	public User getAuthor() {
        Object cache = cache("author");
        if (cache != null) {
            return (User)cache;
        }

		ModelUtils<User> mu = new ModelUtils<User>(User.class);
		if (author != null) {
			return (User) cache("author", mu.find(author));
		}
		return null;
	}
	
	public void setAuthor(User author) {
        invalidate("author");
		this.author = author.getId();
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
        Object cache = cache("task");
        if (cache != null) {
            return (Task) cache;
        }

		ModelUtils<Task> mu = new ModelUtils<Task>(Task.class);
		if (task != null) {
			return (Task) cache("task", mu.find(task));
		}
		return null;
	}
	
	public void setTask(Task task) {
        invalidate("task");
		this.task = task.getId();
	}
    
	// ---------------------
	
	/**
	 * Returns a Task jsoned for the view.
	 * @return ObjectNode the Task jsonsed for Action diffusion.
	 */
	public ObjectNode toJsonView() {
		ObjectNode node = Json.newObject();
        node.put("id",              getId());
		node.put("creationDate",    Application.formater.format(creationDate));
		node.put("content",         content);
        node.put("author",          getAuthor().getUsername());
		return node;
	}

	/**
	 * Returns a Task jsoned for Action diffusion.
	 * @return ObjectNode the Task jsonsed for Action diffusion.
	 */
	public ObjectNode toJsonAction() {
        return toJsonView();
	}
}
