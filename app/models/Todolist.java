package models;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.QubutoModel;
import models.TaskState;

import com.mehteor.db.ModelUtils;

public class Todolist extends QubutoModel {
	/**
	 * Todolist's name.
	 */
	private String name;
	
	/**
	 * The clean name is the name id of this Todolist. (used to identified it in the URL)
	 */
	private String cleanName;
	
	/**
	 * Todolist's description.
	 */
	private String description;

	/**
	 * Creation's date of this todolist.
	 */
	private Date creationDate;
	
	/**
	 * Tags configuration for this todolist.
	 */
	private Map<String, String> tags;
	
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
	
	public Todolist() {
		this.creationDate = new Date();
        this.tags = new HashMap<String,String>();
        this.tags.put("1", "TODO");
        this.tags.put("2", "FEATURE");
        this.tags.put("3", "BUG");
	}

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
	
    @Override
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

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
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
	
	public List<Task> getTasks() {
		ModelUtils<Task> tasks = new ModelUtils<Task>(Task.class);
		return Collections.unmodifiableList(tasks.query("{'todolist': #}", this.getId()));
	}

	public List<Task> getTodoTasks() {
		ModelUtils<Task> tasks = new ModelUtils<Task>(Task.class);
		return Collections.unmodifiableList(tasks.query("{'todolist': #, 'state': #}", this.getId(), TaskState.TODO));
	}

	public List<Task> getDoneTasks() {
		ModelUtils<Task> tasks = new ModelUtils<Task>(Task.class);
		return Collections.unmodifiableList(tasks.query("{'todolist': #, 'state': #}", this.getId(), TaskState.DONE));
	}

    @Override
    public void remove() {
        for (Task task : getTasks()) {
            task.remove();
        }
    }
}
