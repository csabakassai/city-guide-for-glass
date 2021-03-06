package com.doctusoft.cityguide.entity;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.google.common.collect.Lists;
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
public class User {
	@Id
	private String id;
	@Load
	private List<String> tourIds = Lists.newArrayList();
	
	private String actualTourId;
	
}
