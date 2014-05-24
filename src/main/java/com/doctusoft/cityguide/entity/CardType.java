package com.doctusoft.cityguide.entity;

import java.util.UUID;

import lombok.Data;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

@Data
@Unindex
@Cache
@Entity
public class CardType {
	@Id
	private String id = UUID.randomUUID().toString();
	private String text;
	
}