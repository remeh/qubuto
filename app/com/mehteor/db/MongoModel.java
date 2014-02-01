package com.mehteor.db;

import javax.persistence.Id;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

import plugins.JongoPlugin;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simple Mongo model with helpers.
 * 
 * @author Rémy 'remeh' Mathieu
 */
public class MongoModel {

    protected Map<String, Object> cachedObjects  = new HashMap<String, Object>();

    // ---------------------- 

    @Id
    @JsonProperty("_id")
    protected String id;
    
    // ---------------------- 

    protected Object cache(String field) {
        return cachedObjects.get(field);
    }

    protected Object cache(String field, Object object) {
        cachedObjects.put(field, object);
        return object;
    }

    protected Object invalidate(String field) {
        return cachedObjects.remove(field);
    }

	// ---------------------

	public void save() {
        // we don't want to save the cache
        Map<String, Object> save = cachedObjects;
        cachedObjects = null;

		models().save(this);

        // restore the cache
        cachedObjects = save;
	}

	public void remove() {
		models().remove("{_id: #}", this.id);
	}
	
	// ---------------------
	
	public MongoCollection models() {
		Jongo jongo = JongoPlugin.getJongoPlugin().getJongo();
		return jongo.getCollection(String.format("%ss", this.getClass().getSimpleName().toLowerCase()));
	}
	
	public String getId() {
		return id;
	}
}
