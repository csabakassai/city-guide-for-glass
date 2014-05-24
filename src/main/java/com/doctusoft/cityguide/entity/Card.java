package com.doctusoft.cityguide.entity;

import java.util.Map;
import java.util.UUID;

import lombok.Data;
import lombok.experimental.Builder;

import com.google.common.collect.Maps;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Unindex;

@Builder
@Data
@Cache
@Unindex
@Entity
public class Card {
	@Id
	private String id = UUID.randomUUID().toString();
	@Load
	private Ref<CardType> cardType;
	private Map<String, String> properties = Maps.newHashMap();
	
}
