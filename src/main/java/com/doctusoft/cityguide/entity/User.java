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
public class User {
	@Id
	private String email;
	private List<String> tours = Lists.newArrayList();
	
}
