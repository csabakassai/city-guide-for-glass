package com.doctusoft.cityguide.service;

import com.doctusoft.cityguide.entity.User;

public class UserService extends EntityDao<User> {
	
	@Override
	Class<User> getEntityClass() {
		return User.class;
	}
}
