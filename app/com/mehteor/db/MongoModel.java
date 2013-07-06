package com.mehteor.db;

import javax.persistence.Id;

import java.util.Map;
import java.util.HashMap;

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

    private Map<String, Object> cachedObjects  = new HashMap<String, Object>();

    // ---------------------- 

    @Id
    @JsonProperty("_id")
    protected String id;
    
    // ---------------------- 

    protected Object cache(String field) {
        Object cache = cachedObjects.get(field);
        if (cache != null) {
            return cache;
        }
        return null;
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
		models().save(this);
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
