package com.doctusoft.cityguide.entity;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.MenuValue;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindex;

@NoArgsConstructor
@Data
@Unindex
@Cache
@Entity
public class GuideMenuItem {
	
	private String action;
	
	private String id;
	
	private String payload;
	private Boolean removeWhenSelected;
	
	private List<GuideMenuValue> values;
	
	public MenuItem convert() {
		MenuItem menuItem = new MenuItem();
		menuItem.setAction(action);
		menuItem.setId(id);
		menuItem.setPayload(payload);
		menuItem.setValues(Lists.newArrayList(Iterables.transform(values, new Function<GuideMenuValue, MenuValue>() {
			
			@Override
			public MenuValue apply(GuideMenuValue item) {
				return item.convert();
			}
		})));
		return menuItem;
	}
}
