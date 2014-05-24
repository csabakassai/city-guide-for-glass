package com.doctusoft.cityguide.service;


public class TourService extends EntityDao<TourService> {
	
	@Override
	Class<TourService> getEntityClass() {
		return TourService.class;
	}
}
