package com.doctusoft.cityguide.entity;

import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.google.appengine.api.datastore.GeoPt;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Unindex;

@NoArgsConstructor
@Data
@Unindex
@Cache
@Entity
public class Place {
	@Id
	private String id;
	private String name;
	private List<String> pictureUrls = Lists.newArrayList();
	@Load
	private List<Ref<Card>> cards = Lists.newArrayList();
	
	private GeoPt geoPt;
	
	public Place(String id, String name, List<String> pictureUrls, List<Ref<Card>> cards) {
		super();
		this.id = Objects.firstNonNull(id, UUID.randomUUID().toString());
		this.name = name;
		this.pictureUrls = pictureUrls;
		this.cards = cards;
	}

	public List<Ref<Card>> getCards() {
		return cards;
	}

	public void setCards(List<Ref<Card>> cards) {
		this.cards = cards;
	}
	
	
	
}
