package com.doctusoft.cityguide.service;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.googlecode.objectify.ObjectifyService;

public abstract class EntityDao<T> {
	
	public EntityDao() {
		ObjectifyService.register(getEntityClass());
	}
	
	abstract Class<T> getEntityClass();
	
	public T save(T card) {
		ofy().save().entity(card);
		return card;
	}
	
	public T load(String id) {
		return ofy().load().type(getEntityClass()).id(id).now();
	}
}
