package com.doctusoft.cityguide.service;

import com.googlecode.objectify.ObjectifyService;

public class BaseService {
	
	public static <T> T loadEntityById(Class<T> entityClass, String id) {
		return ObjectifyService.ofy().load().type(entityClass).id(id).now();
	}

}
