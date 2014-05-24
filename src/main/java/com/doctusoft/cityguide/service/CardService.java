package com.doctusoft.cityguide.service;

import com.doctusoft.cityguide.entity.Card;

public class CardService extends EntityDao<Card> {
	
	@Override
	Class<Card> getEntityClass() {
		return Card.class;
	}
}
