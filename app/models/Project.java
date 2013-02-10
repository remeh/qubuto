package models;

import java.util.Date;

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
	 * Project's description.
	 */
	private String description;
	
	/**
	 * Creation's date.
	 */
	private Date creationDate;
	
//	/**
//	 * This project todo lists
//	 */
//	private Set<String> todolists;
//	@JsonIgnore
//	private ReferenceUtils<Todolist> ruTodolist = new ReferenceUtils<Todolist>(Todolist.class);
	
	// ---------------------
	
	public Project() {
	}

	// ---------------------

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
	
//	public void addTodolist(Todolist todolist)
//	{
//		todolists = ruTodolist.add(todolists, todolist);
//	}
//	
//	public void removeTodolist(Todolist todolist)
//	{
//		todolists = ruTodolist.remove(todolists, todolist);
//	}
//	
//	public Set<Todolist> getTodolists() {
//		return Collections.unmodifiableSet(ruTodolist.gets(todolists));
//	}
}
