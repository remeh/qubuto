package com.mehteor.db;

import java.util.HashSet;
import java.util.Set;

public class ReferenceUtils<T extends MongoModel> {
	private final Class<T> type;

	public ReferenceUtils(Class<T> type) {
		this.type = type;
	}
	
	// ---------------------
	
	public Set<String> add(Set<String> objects, T object)
	{
		if (object == null || object.id == null)
		{
			return objects;
		}
		
		if (objects == null) {
			objects = new HashSet<String>();
		}
		
		objects.add(object.id);
		return objects;
	}
	
	public Set<String> remove(Set<String> objects, T object)
	{
		if (objects == null) {
			return objects;
		}
		
		objects.remove(object.id);
		return objects;
	}
	
	public Set<T> gets(Set<String> objects) {
		Set<T> results = new HashSet<T>();
		for (String id : objects)
		{
			ModelUtils<T> modelUtils = new ModelUtils<T>(type);
			T t = modelUtils.find(id);
			if (t != null) {
				results.add(t);
			}
		}
		return results;
	}

}
