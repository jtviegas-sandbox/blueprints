package com.tgedr.labs.microservices.blueprint.store.impl.dynamodb;

import lombok.Data;

import java.util.List;

@Data
class Solution {

	private List<Item> items;
	private Integer time;

	public static Solution from(List<Item> items, Integer time){
		Solution r = new Solution();
		r.setItems(items);
		r.setTime(time);
		return r;
	}

}
