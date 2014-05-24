package com.doctusoft.cityguide.service;

import java.util.List;

import com.doctusoft.cityguide.entity.Place;
import com.google.api.services.mirror.model.Location;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.search.GeoPoint;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Query;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.SortOptions;
import com.google.common.base.Preconditions;

public class PlaceService extends EntityDao<Place> {
	
	
	private static final String INDEX_NAME = "places";
	
	private Double threshold = new Double(1);
	
	@Override
	Class<Place> getEntityClass() {
		return Place.class;
	}
	
	static Index getIndex() {
	    IndexSpec indexSpec = IndexSpec.newBuilder().setName(INDEX_NAME).build();
	    return SearchServiceFactory.getSearchService().getIndex(indexSpec);
	  }

	
	
	
	public Place isPlaceNearBy(Location location) {
		
		int distanceInMeters = 20;
		String geoPoint = "geopoint(" + location.getLatitude() + ", " + location.getLongitude() + ")";

	    String query = "distance(location, " + geoPoint + ") < " + distanceInMeters;
	    String locExpr = "distance(location, " + geoPoint + ")";

	    SortExpression sortExpr = SortExpression.newBuilder()
	        .setExpression(locExpr)
	        .setDirection(SortExpression.SortDirection.ASCENDING)
	        .setDefaultValueNumeric(distanceInMeters + 1)
	        .build();
	    Query searchQuery = Query.newBuilder().setOptions(QueryOptions.newBuilder()
	        .setSortOptions(SortOptions.newBuilder().addSortExpression(sortExpr))).build(query);
	    Results<ScoredDocument> results = getIndex().search(searchQuery);

	    Place place = null;
	    if(results.iterator().hasNext()) {
	    	ScoredDocument document = results.iterator().next();
	    	String id = document.getOnlyField("id").getText();
	    	
	    	place = load(id);
	    	Preconditions.checkNotNull(place);
	    } 
	    
	    return place;
	}
	
	
	
	
}
