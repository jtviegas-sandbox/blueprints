package com.tgedr.labs.microservices.blueprint.store.entity.dyndb;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import lombok.Data;

import java.util.List;

@Data
public class Problem {

	private Integer capacity;
	private List<Item> items;

	@DynamoDBIgnore
	public static Problem from(Integer capacity, List<Item> items){
		Problem r = new Problem();
		r.setCapacity(capacity);
		r.setItems(items);
		return r;
	}
}
