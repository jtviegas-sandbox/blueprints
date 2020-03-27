package com.tgedr.labs.microservices.blueprint.model;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.List;

@Data
public class Solution implements Serializable {

	@Setter(AccessLevel.NONE)
	@NotNull
	private final List<Item> items;
	@Setter(AccessLevel.NONE)
	@Positive
	private final Integer time;

	public static Solution from(final List<Item> items, final Integer time){
		return new Solution(items,  time);
	}

	public Solution(final List<Item> items, final Integer time) {
		this.items = ImmutableList.copyOf(items);
		this.time = time;
	}

}
