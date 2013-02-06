package models;

import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

import com.fasterxml.jackson.annotation.JsonProperty;

import uk.co.panaxiom.playjongo.PlayJongo;

/**
 * Simple Mongo model class to simplify the use of Jongo.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public abstract class MongoModel {
	@JsonProperty("_id")
    public ObjectId id;

	// ---------------------
	
	public abstract String getCollectionName();
	
	// ---------------------
	
    public void insert() {
    	all().save(this);
    }

    public void remove() {
    	all().remove(this.id);
    }
    
    public MongoCollection all() {
    	return getCollection(getCollectionName());
    }	

    public Project findByName(String name) {
        return all().findOne("{_id: #}", name).as(Project.class);
    }

    // ---------------------
    
	public static MongoCollection getCollection(String collectionName) {
		return PlayJongo.getCollection(collectionName);
	}
}
