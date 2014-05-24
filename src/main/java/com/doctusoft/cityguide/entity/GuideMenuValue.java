package com.doctusoft.cityguide.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.google.api.services.mirror.model.MenuValue;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindex;

@NoArgsConstructor
@Data
@Unindex
@Cache
@Entity
public class GuideMenuValue {
	private String displayName;
	private String iconUrl;
	private String state;
	
	public MenuValue convert() {
		MenuValue menuValue = new MenuValue();
		menuValue.setDisplayName(displayName);
		menuValue.setIconUrl(iconUrl);
		menuValue.setState(state);
		return menuValue;
	}
}
