package com.doctusoft.cityguide.service;

import java.util.List;
import java.util.logging.Level;

import lombok.extern.java.Log;

import com.google.api.services.mirror.model.Location;
import com.googlecode.placesapiclient.client.argument.ArgumentMap;
import com.googlecode.placesapiclient.client.entity.Place;
import com.googlecode.placesapiclient.client.service.impl.PlacesServiceImpl;

@Log
public class MapsService {
	
	public void search(Location location, String query) {
		try {
			ArgumentMap argumentMap = new ArgumentMap("AIzaSyD9bBQsLWwm0McpeGb2u5UQ77sQFcgzKRY");
			argumentMap.putLocation(location.getLatitude(), location.getLongitude());
			argumentMap.putOpenNow(true);
			argumentMap.putKeyword(query);
			argumentMap.putRadius(1000);
			argumentMap.putSensor(false);
			PlacesServiceImpl placesServiceImpl = new PlacesServiceImpl("AIzaSyD9bBQsLWwm0McpeGb2u5UQ77sQFcgzKRY");
			placesServiceImpl.init();
			List<Place> places = placesServiceImpl.placeNearbySearchRequest(argumentMap);
			System.out.println("places: " + places);
			for (Place place : places) {
				System.out.println("place: " + place.getName());
			}
		} catch (Exception e) {
			log.log(Level.SEVERE, "error finding places", e);
		}
	}
}
