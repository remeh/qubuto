package models;

import java.util.Date;

/**
 * Projects.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public class Project extends MongoModel
{
	/**
	 * Project's name.
	 */
	public String name;
	
	/**
	 * Project's description.
	 */
	public String description;
	
	/**
	 * Creation's date.
	 */
	public Date creationDate;
	
	// ---------------------
	
	public Project()
	{
	}

	@Override
	public String getCollectionName() {
		return "project";
	}
}
