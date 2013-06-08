package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.text.SimpleDateFormat;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

import com.mehteor.db.ModelUtils;

import models.Comment;
import models.QubutoModel;

import controllers.Application;

/**
 * A task in a todolist.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public class Task extends QubutoModel {
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
	private TaskState state;
	
	// ---------------------
	
	public Task() {
        tags = new HashSet<String>();
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

	public TaskState getState() {
		return state;
	}

	public void setState(TaskState state) {
		this.state = state;
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

    public User getCreator() {
        return getAuthor();
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
	
	public List<Comment> getComments() {
		ModelUtils<Comment> comments = new ModelUtils<Comment>(Comment.class);
		return Collections.unmodifiableList(comments.query("{'task': #}", this.getId()));
	}
    
	// ---------------------
	
	/**
	 * Returns a Task jsoned for the view.
	 * @return ObjectNode the Task jsonsed for Action diffusion.
	 */
	public ObjectNode toJsonView() {
		ObjectNode node = Json.newObject();
        node.put("id",              id);
		node.put("title",           title);
		node.put("creationDate",    Application.formater.format(creationDate));
		node.put("content",         content);
		node.put("state",           state.toString());
		node.put("position",        position);
		node.put("tags",            Json.toJson(tags));
		node.put("comments",        Json.toJson(generateCommentsNode()));
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

    // ---------------------- 

    /**
     * Generates an ArrayNode of the comments.
     */
    private ArrayNode generateCommentsNode() {
        ArrayNode array = new ObjectMapper().createArrayNode();
        List<Comment> comments = getComments();
        for (Comment comment : comments) {
            array.add(comment.toJsonView());
        }
        return array;
    }
}

