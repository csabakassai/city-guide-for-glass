package com.doctusoft.cityguide.entity;

import java.util.List;
import java.util.UUID;

import lombok.Data;

import com.google.common.collect.Lists;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Unindex;

@Data
@Unindex
@Cache
@Entity
public class Place {
	@Id
	private String id = UUID.randomUUID().toString();
	private String name;
	private List<String> pictureUrls = Lists.newArrayList();
	@Load
	private List<Ref<Card>> cards = Lists.newArrayList();
	
}
