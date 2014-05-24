package com.doctusoft.cityguide.service;

import com.doctusoft.cityguide.entity.Tour;

public class TourService extends EntityDao<Tour> {
	
	@Override
	Class<Tour> getEntityClass() {
		return Tour.class;
	}
}
