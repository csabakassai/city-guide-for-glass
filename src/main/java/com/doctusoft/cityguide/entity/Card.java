package com.doctusoft.cityguide.entity;

import java.util.Map;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Unindex;

@NoArgsConstructor
@Builder
@Data
@Cache
@Unindex
@Entity
public class Card {
	@Id
	private String id;
	@Load
	private String cardTypeId;
	private Map<String, String> properties = Maps.newHashMap();
	
	public Card(String id, String cardTypeId, Map<String, String> properties) {
		super();
		this.id = Objects.firstNonNull(id, UUID.randomUUID().toString());
		this.cardTypeId = cardTypeId;
		this.properties = properties;
	}
}
