package com.doctusoft.cityguide.service;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.logging.Level;

import lombok.extern.java.Log;

import com.doctusoft.cityguide.AuthUtil;
import com.doctusoft.cityguide.MirrorClient;
import com.doctusoft.cityguide.entity.Card;
import com.doctusoft.cityguide.entity.CardType;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;

@Log
public class TimeLineService {
	
	private CardService cardService = new CardService();
	
	public void sendTimeLineItem(String user, String cardId) {
		Card card = cardService.load(cardId);
		CardType cardType = card.getCardType().get();
		String html = cardType.getText();
		
		for (Entry<String, String> entry : card.getProperties().entrySet()) {
			html = html.replace("${" + entry.getKey() + "}", entry.getValue());
		}
		
		TimelineItem timelineItem = new TimelineItem();
		timelineItem.setHtml(html);
		
		// Triggers an audible tone when the timeline item is received
		timelineItem.setNotification(new NotificationConfig().setLevel("DEFAULT"));
		
		try {
			MirrorClient.insertTimelineItem(AuthUtil.getCredential(user), timelineItem);
		} catch (IOException e) {
			log.log(Level.SEVERE, "ops", e);
		}
	}
}
