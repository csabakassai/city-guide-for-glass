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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.doctusoft.cityguide.entity.Card;
import com.doctusoft.cityguide.entity.CardType;
import com.doctusoft.cityguide.entity.Place;
import com.doctusoft.cityguide.entity.Tour;
import com.doctusoft.cityguide.entity.User;
import com.doctusoft.cityguide.service.CardService;
import com.doctusoft.cityguide.service.CardTypeDao;
import com.doctusoft.cityguide.service.PlaceService;
import com.doctusoft.cityguide.service.TimeLineService;
import com.doctusoft.cityguide.service.TourService;
import com.doctusoft.cityguide.service.UserService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.mirror.model.Command;
import com.google.api.services.mirror.model.Contact;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.MenuValue;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.appengine.api.datastore.GeoPt;
import com.google.common.collect.Lists;

/**
 * Handles POST requests from index.jsp
 * 
 * @author Jenny Murphy - http://google.com/+JennyMurphy
 */
public class MainServlet extends HttpServlet {
	
	
	public static String USER_ID = "104678827155686779010";
	
	public static final String TOUR_ID = "f0f87890-e402-11e3-ac10-0800200c9a66";
	
	private CardService cardService = new CardService();
	private CardTypeDao cardTypeService = new CardTypeDao();
	private PlaceService placeService = new PlaceService();
	private TourService tourService = new TourService();
	private UserService userService = new UserService();
	private TimeLineService timelineService = new TimeLineService();
	
	/**
	 * Private class to process batch request results.
	 * <p/>
	 * For more information, see https://code.google.com/p/google-api-java-client/wiki/Batch.
	 */
	private final class BatchCallback extends JsonBatchCallback<TimelineItem> {
		private int success = 0;
		private int failure = 0;
		
		@Override
		public void onSuccess(TimelineItem item, HttpHeaders headers) throws IOException {
			++success;
		}
		
		@Override
		public void onFailure(GoogleJsonError error, HttpHeaders headers) throws IOException {
			++failure;
			LOG.info("Failed to insert item: " + error.getMessage());
		}
	}
	
	private static final Logger LOG = Logger.getLogger(MainServlet.class.getSimpleName());
	public static final String CONTACT_ID = "com.google.glassware.contact.java-quick-start";
	public static final String CONTACT_NAME = "Java Quick Start";
	
	private static final String PAGINATED_HTML =
			"<article class='auto-paginate'>"
					+ "<h2 class='blue text-large'>Did you know...?</h2>"
					+ "<p>Cats are <em class='yellow'>solar-powered.</em> The time they spend napping in "
					+ "direct sunlight is necessary to regenerate their internal batteries. Cats that do not "
					+ "receive sufficient charge may exhibit the following symptoms: lethargy, "
					+ "irritability, and disdainful glares. Cats will reactivate on their own automatically "
					+ "after a complete charge cycle; it is recommended that they be left undisturbed during "
					+ "this process to maximize your enjoyment of your cat.</p><br/><p>"
					+ "For more cat maintenance tips, tap to view the website!</p>"
					+ "</article>";
	
	public static final String TEMPLATE = "<article class=\"photo\">"
			+ "<img src=\"${picture}\" width=\"100%\" height=\"100%\">"
			+ "</article>";
	public static final String TEMPLATE_HTML =
			"<article>"
					+ "<h2 class='white text-large'>${title}</h2>"
					+ "<p style='color=black background-color=white'>${content}</p>"
					+ "</article>";
	
	/**
	 * Do stuff when buttons on index.jsp are clicked
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		
		String userId = AuthUtil.getUserId(req);
		System.out.println(userId);
		Credential credential = AuthUtil.newAuthorizationCodeFlow().loadCredential(userId);
		String message = "";
		
		if (req.getParameter("operation").equals("initData")) {
			
			tourService.deleteAll();
			cardService.deleteAll();
			placeService.deleteAll();
			
			List<Place> places = Lists.newArrayList();
			CardType type = cardTypeService.save(CardType.builder().text(TEMPLATE).build());
			
			Place hosok = createHosok(places, type);
			createBazilika(places, type);
//			createParl(places, type);
			
			Card starter = new Card();
			starter.setCardTypeId(type.getId());
			starter.setNextPlaceMenuItemTitle("Start tour");
			starter.getProperties().put("title", "Start First tour");
			starter.getProperties().put("picture", "https://docs.google.com/a/doctusoft.com/uc?authuser=0&id=0B5jtLBwq9wEYT1pmWGJocVhvbTQ&export=download");
			cardService.save(starter);
			
			Tour tour = new Tour("First Tour", places, starter.getId());
			tour.setId(TOUR_ID);
			tourService.save(tour);
			
			User user = new User();
			user.setId(USER_ID);
			user.getTourIds().add(tour.getId());
			userService.save(user);
			
			
		} else if (req.getParameter("operation").equals("insertSubscription")) {
			
			// subscribe (only works deployed to production)
			try {
				MirrorClient.insertSubscription(credential, WebUtil.buildUrl(req, "/notify"), userId,
						req.getParameter("collection"));
				message = "Application is now subscribed to updates.";
			} catch (GoogleJsonResponseException e) {
				LOG.warning("Could not subscribe " + WebUtil.buildUrl(req, "/notify") + " because "
						+ e.getDetails().toPrettyString());
				message = "Failed to subscribe. Check your log for details";
			}
			
		} else if (req.getParameter("operation").equals("deleteSubscription")) {
			
			// subscribe (only works deployed to production)
			MirrorClient.deleteSubscription(credential, req.getParameter("subscriptionId"));
			
			message = "Application has been unsubscribed.";
			
		} else if (req.getParameter("operation").equals("insertItem")) {
			LOG.fine("Inserting Timeline Item");
			TimelineItem timelineItem = new TimelineItem();
			
			if (req.getParameter("message") != null) {
				timelineItem.setText(req.getParameter("message"));
			}
			
			// Triggers an audible tone when the timeline item is received
			timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
			
			if (req.getParameter("imageUrl") != null) {
				// Attach an image, if we have one
				URL url = new URL(req.getParameter("imageUrl"));
				String contentType = req.getParameter("contentType");
				MirrorClient.insertTimelineItem(credential, timelineItem, contentType, url.openStream());
				
			} else if (req.getParameter("audioUrl") != null) {
				// Attach an audio, if we have one
				URL url = new URL(req.getParameter("audioUrl"));
				String contentType = req.getParameter("contentType");
				MirrorClient.insertTimelineItem(credential, timelineItem, contentType, url.openStream());
			}
			else {
				MirrorClient.insertTimelineItem(credential, timelineItem);
			}
			
			message = "A timeline item has been inserted.";
			
		} else if (req.getParameter("operation").equals("insertPaginatedItem")) {
			LOG.fine("Inserting Timeline Item");
			TimelineItem timelineItem = new TimelineItem();
			timelineItem.setHtml(PAGINATED_HTML);
			
			List<MenuItem> menuItemList = new ArrayList<MenuItem>();
			menuItemList.add(new MenuItem().setAction("OPEN_URI").setPayload(
					"https://www.google.com/search?q=cat+maintenance+tips"));
			timelineItem.setMenuItems(menuItemList);
			
			// Triggers an audible tone when the timeline item is received
			timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
			
			MirrorClient.insertTimelineItem(credential, timelineItem);
			
			message = "A timeline item has been inserted.";
			
		} else if (req.getParameter("operation").equals("insertItemWithAction")) {
			LOG.fine("Inserting Timeline Item");
			TimelineItem timelineItem = new TimelineItem();
			timelineItem.setText("Tell me what you had for lunch :)");
			
			List<MenuItem> menuItemList = new ArrayList<MenuItem>();
			// Built in actions
			menuItemList.add(new MenuItem().setAction("REPLY"));
			menuItemList.add(new MenuItem().setAction("READ_ALOUD"));
			
			// And custom actions
			List<MenuValue> menuValues = new ArrayList<MenuValue>();
			menuValues.add(new MenuValue().setIconUrl(WebUtil.buildUrl(req, "/static/images/drill.png"))
					.setDisplayName("Drill In"));
			menuItemList.add(new MenuItem().setValues(menuValues).setId("drill").setAction("CUSTOM"));
			
			timelineItem.setMenuItems(menuItemList);
			timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
			
			MirrorClient.insertTimelineItem(credential, timelineItem);
			
			message = "A timeline item with actions has been inserted.";
			
		} else if (req.getParameter("operation").equals("insertContact")) {
			if (req.getParameter("iconUrl") == null || req.getParameter("name") == null) {
				message = "Must specify iconUrl and name to insert contact";
			} else {
				// Insert a contact
				LOG.fine("Inserting contact Item");
				Contact contact = new Contact();
				contact.setId(req.getParameter("id"));
				contact.setDisplayName(req.getParameter("name"));
				contact.setImageUrls(Lists.newArrayList(req.getParameter("iconUrl")));
				contact.setAcceptCommands(Lists.newArrayList(new Command().setType("TAKE_A_NOTE")));
				MirrorClient.insertContact(credential, contact);
				
				message = "Inserted contact: " + req.getParameter("name");
			}
			
		} else if (req.getParameter("operation").equals("deleteContact")) {
			
			// Insert a contact
			LOG.fine("Deleting contact Item");
			MirrorClient.deleteContact(credential, req.getParameter("id"));
			
			message = "Contact has been deleted.";
			
		} else if (req.getParameter("operation").equals("insertItemAllUsers")) {
			if (req.getServerName().contains("glass-java-starter-demo.appspot.com")) {
				message = "This function is disabled on the demo instance.";
			}
			
			// Insert a contact
			List<String> users = AuthUtil.getAllUserIds();
			LOG.info("found " + users.size() + " users");
			if (users.size() > 10) {
				// We wouldn't want you to run out of quota on your first day!
				message =
						"Total user count is " + users.size() + ". Aborting broadcast " + "to save your quota.";
			} else {
				TimelineItem allUsersItem = new TimelineItem();
				allUsersItem.setText("Hello Everyone!");
				
				BatchRequest batch = MirrorClient.getMirror(null).batch();
				BatchCallback callback = new BatchCallback();
				
				// TODO: add a picture of a cat
				for (String user : users) {
					Credential userCredential = AuthUtil.getCredential(user);
					MirrorClient.getMirror(userCredential).timeline().insert(allUsersItem)
							.queue(batch, callback);
				}
				
				batch.execute();
				message =
						"Successfully sent cards to " + callback.success + " users (" + callback.failure
								+ " failed).";
			}
			
		} else if (req.getParameter("operation").equals("deleteTimelineItem")) {
			
			// Delete a timeline item
			LOG.fine("Deleting Timeline Item");
			MirrorClient.deleteTimelineItem(credential, req.getParameter("itemId"));
			
			message = "Timeline Item has been deleted.";
			
		} else {
			String operation = req.getParameter("operation");
			LOG.warning("Unknown operation specified " + operation);
			message = "I don't know how to do that";
		}
		WebUtil.setFlash(req, message);
		res.sendRedirect(WebUtil.buildUrl(req, "/"));
	}
	
//	private void createParl(List<Place> places, CardType type) {
//		Place parl = new Place();
//		parl.setLocation(new GeoPt(47.50712069999999f, 19.04566879999993f));
//		places.add(parl);
//		
//		Card parl1 = new Card();
//		parl1.setCardTypeId(type.getId());
//		parl1.getProperties().put("title", "Hősök 1");
//		parl1.getProperties().put("picture", "https://lh4.googleusercontent.com/bVb1j9XxfXTLErZgSeVjqfQ1MkKoneMs8Kpv56TPxeTZ-E0RJ8y1f_yVT7mZQtC93w=w1743-h824");
//		parl1.setVideoURL("https://docs.google.com/a/doctusoft.com/file/d/0B-yVBA23vGL7UlFsVGFqVjVDRmc/preview");
//		parl.getCardIds().add(parl1.getId());
//		cardService.save(parl1);
//		
//		placeService.save(parl);
//	}
	
	private void createBazilika(List<Place> places, CardType type) {
		Place bazilika = new Place();
		bazilika.setLocation(new GeoPt(47.5008902f, 19.05398869999999f));
		places.add(bazilika);
		
		System.out.println(bazilika);
		
		Card bazilika1 = new Card();
		bazilika1.setCardTypeId(type.getId());
		bazilika1.getProperties().put("picture", "https://docs.google.com/a/doctusoft.com/uc?authuser=0&id=0B5jtLBwq9wEYRzRZeVh4cGJCZkU&export=download");
		bazilika1.setVideoURL("https://docs.google.com/a/doctusoft.com/file/d/0B-yVBA23vGL7UlFsVGFqVjVDRmc/preview");
		cardService.save(bazilika1);
		bazilika.getCardIds().add(bazilika1.getId());
		
		placeService.save(bazilika);
	}
	
	private Place createHosok(List<Place> places, CardType type) {
		Place hosok = new Place();
		places.add(hosok);
		hosok.setLocation(new GeoPt(47.5149429f, 19.077862600000003f));
		
		Card hosok1 = new Card();
		hosok1.setCardTypeId(type.getId());
		hosok1.getProperties().put("picture", "https://docs.google.com/a/doctusoft.com/uc?authuser=0&id=0B5jtLBwq9wEYT1pmWGJocVhvbTQ&export=download");
		hosok1.setVideoURL("https://docs.google.com/a/doctusoft.com/file/d/0B-yVBA23vGL7OUk1aTc0MUZEckk/preview");
		cardService.save(hosok1);
		hosok.getCardIds().add(hosok1.getId());
		
		placeService.save(hosok);
		return hosok;
	}
}
