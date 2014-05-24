package com.doctusoft.cityguide.service;

import com.doctusoft.cityguide.entity.Place;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.GeoPoint;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;

public class PlaceService extends EntityDao<Place> {
	
	@Override
	Class<Place> getEntityClass() {
		return Place.class;
	}
	
	@Override
	public Place save(Place card) {
		card = super.save(card);
		Document indexEntry = Document.newBuilder().setId(card.getId())
				.addField(Field.newBuilder().setName("location").setGeoPoint(new GeoPoint(card.getLocation().getLatitude(), card.getLocation().getLongitude())).build())
				.addField(Field.newBuilder().setName("name").setText(card.getName()).build())
				.build();
		IndexSpec indexSpec = IndexSpec.newBuilder().setName("places").build();
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
		index.put(indexEntry);
		return card;
	}
}
