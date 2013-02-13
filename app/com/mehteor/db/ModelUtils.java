package com.mehteor.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import plugins.JongoPlugin;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

public class ModelUtils<T> {
	private final Class<T> type;

	public ModelUtils(Class<T> type) {
		this.type = type;
	}

	public MongoCollection models() {
        Jongo jongo = JongoPlugin.getJongoPlugin().getJongo();
		return jongo.getCollection(String.format("%ss", type.getSimpleName().toLowerCase()));
	}

	public List<T> all() {

		List<T> list = new ArrayList<T>();
		Iterator<T> it = models().find().as(type).iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}

	public T find(String id) {
		return models().findOne("{_id: #}", id).as(type);
	}

	public List<T> query(String query) {
		List<T> list = new ArrayList<T>();
		Iterator<T> it = models().find(query).as(type).iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}
}
