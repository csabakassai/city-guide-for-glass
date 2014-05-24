package com.doctusoft.cityguide.entity;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Builder;

import com.google.common.base.Objects;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

@NoArgsConstructor
@Builder
@Data
@Unindex
@Cache
@Entity
public class CardType {
	@Id
	private String id;
	private String text;
	
	public CardType(String id, String text) {
		super();
		this.id = Objects.firstNonNull(id, UUID.randomUUID().toString());
		this.text = text;
	}
}
