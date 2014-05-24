package com.doctusoft.cityguide.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import com.google.appengine.api.datastore.GeoPt;
import com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

@AllArgsConstructor
@Builder
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
	private List<String> cards = Lists.newArrayList();
	private GeoPt location;
	
}
