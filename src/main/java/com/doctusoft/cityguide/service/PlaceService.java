package com.doctusoft.cityguide.service;

import com.doctusoft.cityguide.entity.Card;
import com.doctusoft.cityguide.entity.Place;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Document.Builder;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.GeoPoint;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.SearchServiceFactory;
import com.googlecode.objectify.Ref;

public class PlaceService extends EntityDao<Place> {
	
	@Override
	Class<Place> getEntityClass() {
		return Place.class;
	}
	
	@Override
	public Place save(Place place) {
		place = super.save(place);
		Builder builder = Document.newBuilder().setId(place.getId())
				.addField(Field.newBuilder().setName("location").setGeoPoint(new GeoPoint(place.getLocation().getLatitude(), place.getLocation().getLongitude())).build())
				.addField(Field.newBuilder().setName("name").setText(place.getName()).build());
		for (Ref<Card> card : place.getCards()) {
			for (String description : card.get().getProperties().values()) {
				if (description != null) {
					builder.addField(Field.newBuilder().setName("description").setText(description).build());
				}
			}
		}
		Document indexEntry = builder.build();
		IndexSpec indexSpec = IndexSpec.newBuilder().setName("places").build();
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
		index.put(indexEntry);
		return place;
	}
}
