package com.tgedr.labs.microservices.blueprint.store.entity.dyndb;

import lombok.Data;

import java.util.List;

@Data
public class Solution {

	private List<Item> items;
	private Integer time;

	public static Solution from(List<Item> items, Integer time){
		Solution r = new Solution();
		r.setItems(items);
		r.setTime(time);
		return r;
	}

}
