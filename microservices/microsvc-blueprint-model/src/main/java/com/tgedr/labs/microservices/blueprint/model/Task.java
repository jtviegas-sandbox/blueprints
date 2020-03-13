package com.tgedr.labs.microservices.blueprint.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Task implements Serializable {

	@Setter(AccessLevel.NONE)
	@NotNull
	private final String id;

	@Setter(AccessLevel.NONE)
	@NotNull
	private final Problem problem;

	private Solution solution;
	// status x timestamp
	private final Map<Status,Long> statuses = new LinkedHashMap<>();

	public static Task from(String id, Problem problem){
		return new Task(id, problem);
	}

}
