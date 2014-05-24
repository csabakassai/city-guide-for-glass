package com.doctusoft.cityguide.service;

import com.doctusoft.cityguide.entity.Place;

public class PlaceService extends EntityDao<Place> {
	
	@Override
	Class<Place> getEntityClass() {
		return Place.class;
	}
}
