package com.tgedr.labs.microservices.blueprint.store.dynamodb.entities;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgedr.labs.microservices.blueprint.model.Status;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Data
@DynamoDBTable(tableName = "Task")
public class Task {

	@Autowired
	private static ObjectMapper mapper = new ObjectMapper();

	@DynamoDBHashKey
	private String id;
	@DynamoDBAttribute
	@DynamoDBTypeConverted(converter = ProblemTypeConverter.class)
	private Problem problem;
	@DynamoDBAttribute
	@DynamoDBTypeConverted(converter = SolutionTypeConverter.class)
	private Solution solution;
	@DynamoDBAttribute
	@DynamoDBTypeConverted(converter = StatusMapTypeConverter.class)
	private Map<Status,Long> statuses;

	static Task from(String id, Problem problem){
		Task task = new Task();
		task.setId(id);
		task.setProblem(problem);
		return task;
	}

	static final public class ProblemTypeConverter implements DynamoDBTypeConverter<String, Problem> {

		@SneakyThrows
		@Override
		public String convert(Problem object) {
			return mapper.writeValueAsString(object);
		}

		@SneakyThrows
		@Override
		public Problem unconvert(String s) {
			return mapper.readValue(s, Problem.class);
		}
	}

	static final public class SolutionTypeConverter implements DynamoDBTypeConverter<String, Solution> {

		@SneakyThrows
		@Override
		public String convert(Solution object) {
			return mapper.writeValueAsString(object);
		}

		@SneakyThrows
		@Override
		public Solution unconvert(String s) {
			return mapper.readValue(s, Solution.class);
		}
	}

	static final public class StatusMapTypeConverter implements DynamoDBTypeConverter<String, Map<Status,Long>> {

		@SneakyThrows
		@Override
		public String convert(Map<Status, Long> object) {
			return mapper.writeValueAsString(object);
		}

		@SneakyThrows
		@Override
		public Map<Status,Long> unconvert(String s) {
			return mapper.readValue(s, new TypeReference<Map<Status,Long>>(){});
		}
	}

}


