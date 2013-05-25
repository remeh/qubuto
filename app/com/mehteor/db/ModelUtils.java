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
	
	public int remove(String query, Object...parameters) {
		return models().remove(query, parameters).getN();
	}
	
	public long count() {
		return models().count();
	}
	
	public long count(String query, Object... parameters) {
		return models().count(query, parameters);
	}

    /**
     * Launches a query, sort by the given field and return the entry at the bottom of the list.
     * Warning: could return null !
     */
    public T last(String fieldSort, String query, Object... parameters) {
        Iterator<T> it = models().find(query, parameters).sort(String.format("{%s: -1}", fieldSort)).limit(1).as(type).iterator();
        if (it == null || !it.hasNext()) {
            return null;
        }
        return it.next();
    }

    /**
     * Launches a query, sort by the given field and return the entry at the bottom of the list.
     * Warning: could return null !
     */
    public T first(String fieldSort, String query, Object... parameters) {
        Iterator<T> it = models().find(query, parameters).sort(String.format("{%s: 1}", fieldSort)).limit(1).as(type).iterator();
        if (it == null || !it.hasNext()) {
            return null;
        }
        return it.next();
    }
	
	public List<T> query(String query, Object... parameters) {
		List<T> list = new ArrayList<T>();
		Iterator<T> it = models().find(query, parameters).as(type).iterator();
		while (it.hasNext()) {
			list.add(it.next());
		}
		return list;
	}
}
