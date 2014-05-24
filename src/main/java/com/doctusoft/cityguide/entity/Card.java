package com.doctusoft.cityguide.entity;

import java.util.Map;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.google.common.collect.Maps;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

@NoArgsConstructor
@Data
@Cache
@Unindex
@Entity
public class Card {
	@Id
	private String id = UUID.randomUUID().toString();
	private String cardTypeId;
	private String audio;
	private Map<String, String> properties = Maps.newHashMap();
	
	public Map<String, String> getProperties() {
		if (properties == null) {
			properties = Maps.newHashMap();
		}
		return properties;
	}
}
