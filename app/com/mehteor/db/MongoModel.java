package com.mehteor.db;

import javax.persistence.Id;

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
	@Id
	@JsonProperty("_id")
	protected String id;

	// ---------------------
	
	
	
	// ---------------------

	public void save() {
		models().save(this);
	}

	// XXX...
	public void update() {
		this.remove();
		this.save();
	}

	public void remove() {
		models().remove(this.id);
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
