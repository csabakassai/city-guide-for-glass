package com.doctusoft.cityguide.entity;

import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

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
public class Tour {
	@Id
	private String id;
	private String title;
	@Load
	private List<Ref<Place>> places = Lists.newArrayList();
	
	public Tour(String id, String title, List<Ref<Place>> places) {
		super();
		this.id = Objects.firstNonNull(id, UUID.randomUUID().toString());
		this.title = title;
		this.places = places;
	}
}
