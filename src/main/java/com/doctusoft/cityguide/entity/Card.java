package com.doctusoft.cityguide.entity;

import java.util.Map;

import lombok.Data;

import com.google.common.collect.Maps;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

@Data
@Cache
@Unindex
@Entity
public class Card {
	@Id
	private String id;
	private String cardTypeId;
	private Map<String, String> properties = Maps.newHashMap();
	
}
