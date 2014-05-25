/*
 * Copyright (C) 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.doctusoft.cityguide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Random;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doctusoft.cityguide.entity.Card;
import com.doctusoft.cityguide.entity.Place;
import com.doctusoft.cityguide.entity.Tour;
import com.doctusoft.cityguide.service.CardService;
import com.doctusoft.cityguide.service.MapsService;
import com.doctusoft.cityguide.service.PlaceService;
import com.doctusoft.cityguide.service.TimeLineService;
import com.doctusoft.cityguide.service.TourService;
import com.doctusoft.cityguide.service.UserService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.model.Location;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.Notification;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.api.services.mirror.model.UserAction;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Handles the notifications sent back from subscriptions
 * 
 * @author Jenny Murphy - http://google.com/+JennyMurphy
 */
public class NotifyServlet extends HttpServlet {
	
	private static final Logger LOG = Logger.getLogger(NotifyServlet.class.getSimpleName());
	
	private static final String[] CAT_UTTERANCES = {
			"<em class='green'>Purr...</em>",
			"<em class='red'>Hisss... scratch...</em>",
			"<em class='yellow'>Meow...</em>"
	};
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Respond with OK and status 200 in a timely fashion to prevent redelivery
		response.setContentType("text/html");
		Writer writer = response.getWriter();
		writer.append("OK");
		writer.close();
		
		// Get the notification object from the request body (into a string so we
		// can log it)
		BufferedReader notificationReader =
				new BufferedReader(new InputStreamReader(request.getInputStream()));
		String notificationString = "";
		
		// Count the lines as a very basic way to prevent Denial of Service attacks
		int lines = 0;
		while (notificationReader.ready()) {
			notificationString += notificationReader.readLine();
			lines++;
			
			// No notification would ever be this long. Something is very wrong.
			if (lines > 1000) {
				throw new IOException("Attempted to parse notification payload that was unexpectedly long.");
			}
		}
		
		LOG.info("got raw notification " + notificationString);
		
		JsonFactory jsonFactory = new JacksonFactory();
		
		// If logging the payload is not as important, use
		// jacksonFactory.fromInputStream instead.
		Notification notification = jsonFactory.fromString(notificationString, Notification.class);
		
		LOG.info("Got a notification with ID: " + notification.getItemId());
		
		// Figure out the impacted user and get their credentials for API calls
		String userId = notification.getUserToken();
		Credential credential = AuthUtil.getCredential(userId);
		Mirror mirrorClient = MirrorClient.getMirror(credential);
		
		if (notification.getCollection().equals("locations")) {
			LOG.info("Notification of updated location");
			Mirror glass = MirrorClient.getMirror(credential);
			// item id is usually 'latest'
			Location location = glass.locations().get(notification.getItemId()).execute();
			
			new MapsService().search(location, "restaurant");
			
			UserService userService = new UserService();
			// User actualUser = userService.load(userId);
			// Preconditions.checkNotNull(actualUser);
			// String actualTourId = actualUser.getActualTourId();
			String actualTourId = MainServlet.TOUR_ID;
			if (actualTourId != null) {
				TourService tourService = new TourService();
				
				Tour actualTour = tourService.load(actualTourId);
				
				// Place place = placeService.isPlaceNearBy(location);
				Place place = getNextPlace(actualTour);
				if (place != null) {
					actualTour.setActualPlaceId(place.getId());
					Place nextPlace = getNextPlace(actualTour);
					if (nextPlace != null) {
						CardService cardService = new CardService();
						TimeLineService timeLineService = new TimeLineService();
						for (String cardId : Lists.reverse(place.getCardIds())) {
							Card card = cardService.load(cardId);
							Preconditions.checkNotNull(card);
							timeLineService.sendInfoCardItem(userId, card, nextPlace);
						}
					} else {
						LOG.info("Tour ended");
					}
					
				} else {
					LOG.info("place is null");
				}
				
				LOG.info("New location is " + location.getLatitude() + ", " + location.getLongitude());
			}
			
			// This is a location notification. Ping the device with a timeline item
			// telling them where they are.
		} else if (notification.getCollection().equals("timeline")) {
			// Get the impacted timeline item
			TimelineItem timelineItem = mirrorClient.timeline().get(notification.getItemId()).execute();
			LOG.info("Notification impacted timeline item with ID: " + timelineItem.getId());
			
			// If it was a share, and contains a photo, update the photo's caption to
			// acknowledge that we got it.
			if (notification.getUserActions().contains(new UserAction().setType("SHARE"))
					&& timelineItem.getAttachments() != null && timelineItem.getAttachments().size() > 0) {
				LOG.info("It was a share of a photo. Updating the caption on the photo.");
				
				String caption = timelineItem.getText();
				if (caption == null) {
					caption = "";
				}
				
				// Create a new item with just the values that we want to patch.
				TimelineItem itemPatch = new TimelineItem();
				itemPatch.setText("Java Quick Start got your photo! " + caption);
				
				// Patch the item. Notice that since we retrieved the entire item above
				// in order to access the caption, we could have just changed the text
				// in place and used the update method, but we wanted to illustrate the
				// patch method here.
				mirrorClient.timeline().patch(notification.getItemId(), itemPatch).execute();
			} else if (notification.getUserActions().contains(new UserAction().setType("LAUNCH"))) {
				LOG.info("It was a note taken with the 'take a note' voice command. Processing it.");
				
				// Grab the spoken text from the timeline card and update the card with
				// an HTML response (deleting the text as well).
				String noteText = timelineItem.getText();
				String utterance = CAT_UTTERANCES[new Random().nextInt(CAT_UTTERANCES.length)];
				
				timelineItem.setText(null);
				timelineItem.setHtml(makeHtmlForCard("<p class='text-auto-size'>"
						+ "Oh, did you say " + noteText + "? " + utterance + "</p>"));
				timelineItem.setMenuItems(Lists.newArrayList(
						new MenuItem().setAction("DELETE")));
				
				mirrorClient.timeline().update(timelineItem.getId(), timelineItem).execute();
			} else if (notification.getUserActions().contains(new UserAction().setType("GET_MEDIA_INPUT"))) {
				for (MenuItem menuItem : timelineItem.getMenuItems()) {
					String payload = menuItem.getPayload();
					LOG.info("Media input:" + payload);
				}
			} else {

				LOG.warning("I don't know what to do with this notification, so I'm ignoring it.");
			}
		}
	}
	
	private Place getNextPlace(Tour actualTour) {
		int actualIndex = actualTour.getPlaceIds().indexOf(actualTour.getActualPlaceId());
		if (actualIndex < actualTour.getPlaceIds().size() - 1) {
			String nextPlaceId = actualTour.getPlaceIds().get(actualIndex + 1);
			PlaceService placeService = new PlaceService();
			Place nextPlace = placeService.load(nextPlaceId);
			return nextPlace;
		} else {
			return null;
		}
		
	}
	
	/**
	 * Wraps some HTML content in article/section tags and adds a footer
	 * identifying the card as originating from the Java Quick Start.
	 * 
	 * @param content
	 *           the HTML content to wrap
	 * @return the wrapped HTML content
	 */
	private static String makeHtmlForCard(String content) {
		return "<article class='auto-paginate'>" + content
				+ "<footer><p>Java Quick Start</p></footer></article>";
	}
}
