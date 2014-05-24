package com.doctusoft.cityguide.service;

import com.doctusoft.cityguide.entity.CardType;
import com.google.api.services.mirror.model.MenuItem;
import com.googlecode.objectify.ObjectifyService;

public class CardTypeDao extends EntityDao<CardType> {
	
	public CardTypeDao() {
		super();
		ObjectifyService.register(MenuItem.class);
	}
	
	@Override
	Class<CardType> getEntityClass() {
		return CardType.class;
	}
}
