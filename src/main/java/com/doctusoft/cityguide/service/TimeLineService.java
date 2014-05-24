package com.doctusoft.cityguide.service;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.extern.java.Log;

import com.doctusoft.cityguide.AuthUtil;
import com.doctusoft.cityguide.MirrorClient;
import com.doctusoft.cityguide.entity.Card;
import com.doctusoft.cityguide.entity.CardType;
import com.google.api.services.mirror.model.Location;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;

@Log
public class TimeLineService {
	
	public String sendTimeLineItem(String user, Card card) {
		CardType cardType = new CardTypeDao().load(card.getCardTypeId());
		String html = cardType.getText();
		
		for (Entry<String, String> entry : card.getProperties().entrySet()) {
			html = html.replace("${" + entry.getKey() + "}", entry.getValue());
		}
		
		TimelineItem timelineItem = new TimelineItem();
		timelineItem.setHtml(html);
		
		MenuItem menuItem = new MenuItem();
		menuItem.setAction("NAVIGATE");
		
		Location location = new Location();
		location.setLatitude(new Double(47.5096592));
		location.setLongitude(new Double(19.0965185));
		
		timelineItem.setLocation(location);
		
		
		timelineItem.getMenuItems().add(menuItem);
		
		// Triggers an audible tone when the timeline item is received
		timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
		timelineItem.setSpeakableText("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.");
		try {
			MirrorClient.insertTimelineItem(AuthUtil.getCredential(user), timelineItem);
			return timelineItem.getHtml();
		} catch (IOException e) {
			log.log(Level.SEVERE, "ops", e);
			return e.getMessage();
		}
		
	}
}
