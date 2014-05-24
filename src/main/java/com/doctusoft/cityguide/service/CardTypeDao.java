package com.doctusoft.cityguide.service;

import com.doctusoft.cityguide.entity.CardType;

public class CardTypeDao extends EntityDao<CardType> {
	
	@Override
	Class<CardType> getEntityClass() {
		return CardType.class;
	}
}
