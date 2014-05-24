package com.doctusoft.cityguide.service;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.googlecode.objectify.ObjectifyService;

public abstract class EntityDao<T> {
	
	public EntityDao() {
		ObjectifyService.register(getEntityClass());
	}
	
	abstract Class<T> getEntityClass();
	
	public T save(T card) {
		System.out.println("save: " + card);
		ofy().save().entity(card);
		return card;
	}
	
	public T load(String id) {
		return ofy().load().type(getEntityClass()).id(id).now();
	}
	
	public Iterable<T> load(Iterable<String> ids) {
		return ofy().load().type(getEntityClass()).ids(ids).values();
	}
	
	public List<T> loadAll() {
		return ofy().load().type(getEntityClass()).list();
	}
	
	public void deleteAll() {
		List<T> allEntity = loadAll();
		ofy().delete().entities(allEntity);
	}
}
