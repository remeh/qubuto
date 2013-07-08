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

    public String getTag(String tag) {
        return tags.get(tag);
    }

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public Project getProject() {
        Object cache = cache("project");
        if (cache != null) {
            return (Project)cache;
        }

		ModelUtils<Project> mu = new ModelUtils<Project>(Project.class);
		if (project != null) {
			return (Project)cache("project", mu.find(project));
		}

        return null;
	}

	public void setProject(Project project) {
        invalidate("project");
		this.project = project.getId();
	}
	
	public List<Task> getTasks() {
        Object cache = cache("tasks");
        if (cache != null) {
            return (List<Task>)cache;
        }

		ModelUtils<Task> tasks = new ModelUtils<Task>(Task.class);
		return (List<Task>)cache("tasks",Collections.unmodifiableList(tasks.query("{'todolist': #}", this.getId())));
	}

	public List<Task> getTodoTasks() {
        Object cache = cache("todotasks");
        if (cache != null) {
            return (List<Task>)cache;
        }

		ModelUtils<Task> tasks = new ModelUtils<Task>(Task.class);
		return (List<Task>) cache("todotasks", Collections.unmodifiableList(tasks.query("{'todolist': #, 'state': #}", this.getId(), TaskState.TODO)));
	}

	public List<Task> getDoneTasks() {
        Object cache = cache("donetasks");
        if (cache != null) {
            return (List<Task>)cache;
        }

		ModelUtils<Task> tasks = new ModelUtils<Task>(Task.class);
		return (List<Task>) cache("donetasks", Collections.unmodifiableList(tasks.query("{'todolist': #, 'state': #}", this.getId(), TaskState.DONE)));
	}

    @Override
    public void remove() {
        for (Task task : getTasks()) {
            task.remove();
        }
        super.remove();
    }
}
