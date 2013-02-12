package models;

import java.util.Date;
import java.util.Set;

import com.mehteor.db.ModelUtils;
import com.mehteor.db.MongoModel;

public class Todolist extends MongoModel {
	/**
	 * Todolist's name.
	 */
	private String name;

	/**
	 * Creation's date of this todolist.
	 */
	private Date creationDate;
	
	/**
	 * Tags configuration for this todolist.
	 */
	private Set<String> tags;

	/**
	 * In which project is this todolist.
	 */
	// reference //
	private String project;
	
	// ---------------------
	
	public Todolist() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Set<String> getTags() {
		return tags;
	}

	public void setTags(Set<String> tags) {
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
}
