package com.doctusoft.cityguide.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.extern.java.Log;

import com.doctusoft.cityguide.AuthUtil;
import com.doctusoft.cityguide.MirrorClient;
import com.doctusoft.cityguide.entity.Card;
import com.doctusoft.cityguide.entity.CardType;
import com.doctusoft.cityguide.entity.Place;
import com.google.api.services.mirror.model.Location;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.MenuValue;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

@Log
public class TimeLineService {
	
	public String sendInfoCardItem(String user, Card card, Place nextPlace) {
		
//		CardType cardType = card.getCardTypeId().get();
		CardTypeDao cardTypeDao = new CardTypeDao();
		CardType cardType = cardTypeDao.load(card.getCardTypeId());
		String html = cardType.getText();
		
		for (Entry<String, String> entry : card.getProperties().entrySet()) {
			html = html.replace("${" + entry.getKey() + "}", entry.getValue());
		}
		
		TimelineItem timelineItem = new TimelineItem();
		timelineItem.setHtml(html);
		
		MenuItem readOutLoudMenuItem = new MenuItem();
		readOutLoudMenuItem.setAction("READ_ALOUD");
		
		MenuItem nextMenuItem = new MenuItem();
		nextMenuItem.setAction("NAVIGATE");
		timelineItem.setMenuItems(Lists.newArrayList(nextMenuItem));
		
		Iterator<MenuValue> iterator = nextMenuItem.getValues().iterator();
		iterator.next().setDisplayName("Next place");
		Location location = new Location();
		GeoPt persistentLcation = nextPlace.getLocation();
		location.setLatitude(new Double(persistentLcation.getLatitude()));
		location.setLongitude(new Double(persistentLcation.getLongitude()));
		
		timelineItem.setLocation(location);
		
		
		timelineItem.setMenuItems(Lists.newArrayList(readOutLoudMenuItem, nextMenuItem));
		
		// Triggers an audible tone when the timeline item is received
		timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
		try {
			MirrorClient.insertTimelineItem(AuthUtil.getCredential(user), timelineItem);
			return timelineItem.getHtml();
		} catch (IOException e) {
			log.log(Level.SEVERE, "ops", e);
			return e.getMessage();
		}
		
	}
	
}
