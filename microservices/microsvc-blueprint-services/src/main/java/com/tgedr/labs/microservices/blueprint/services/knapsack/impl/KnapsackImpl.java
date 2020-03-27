package com.tgedr.labs.microservices.blueprint.services.knapsack.impl;

import com.tgedr.labs.microservices.blueprint.model.Problem;
import com.tgedr.labs.microservices.blueprint.services.knapsack.exceptions.KnapsackException;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.tgedr.labs.microservices.blueprint.services.knapsack.Knapsack;

import static java.lang.String.format;

@Service
@Slf4j
class KnapsackImpl implements Knapsack {

	@Value("${solver.uri:#{null}}")
	private String uri;


	@Override
	public String submitProblem(Problem problem) throws KnapsackException {
		log.debug("[submitProblem|in] ({})", problem);

		HttpResponse<String> response = Unirest.post(format("%s", uri))
				.header("accept", "application/json")
				.header("Content-Type", "application/json")
				.body(problem)
				.asString();

		if( ! response.isSuccess() )
			throw new KnapsackException(format("failed to submit task: %s", response.getStatusText()));

		String result = response.getBody();
		log.debug("[submitProblem|out] => result: {}", result);
		return result;
	}

}
