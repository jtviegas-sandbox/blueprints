package com.tgedr.labs.microservices.blueprint.model;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;
import java.util.List;

@Data
public class Problem implements Serializable {

	@Setter(AccessLevel.NONE)
	@Positive
	private final Integer capacity;
	@Setter(AccessLevel.NONE)
	@NotNull
	@NotEmpty
	private final List<Item> items;


	public Problem(final Integer capacity, final List<Item> items) {
		this.capacity = capacity;
		this.items = ImmutableList.copyOf(items);
	}

	public static Problem from(final Integer capacity, final List<Item> items) {
		return new Problem(capacity, items);
	}

}
