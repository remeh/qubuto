package com.mehteor.db;

import javax.persistence.Id;

import org.jongo.MongoCollection;

import uk.co.panaxiom.playjongo.PlayJongo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MongoModel {
	@Id
	@JsonProperty("_id")
	public String id;

	public MongoCollection models() {
		return PlayJongo.getCollection(String.format("%ss", this.getClass().getSimpleName().toLowerCase()));
	}

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
}