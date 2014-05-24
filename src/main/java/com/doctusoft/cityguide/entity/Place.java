package com.doctusoft.cityguide.entity;

import java.util.List;

import lombok.Data;

import com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

@Data
@Unindex
@Cache
@Entity
public class Place {
	@Id
	private String id;
	private String name;
	private List<String> pictureIds = Lists.newArrayList();
	private List<String> cardIds = Lists.newArrayList();
	
}
