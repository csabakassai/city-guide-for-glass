package com.doctusoft.cityguide.entity;

import java.util.UUID;

import lombok.Data;
import lombok.experimental.Builder;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Unindex;

@Builder
@Data
@Unindex
@Cache
@Entity
public class CardType {
	@Id
	private String id;
	private String text;
	
	@OnSave
	void onSave() {
		if (id == null) {
			id = UUID.randomUUID().toString();
		}
	}
}
