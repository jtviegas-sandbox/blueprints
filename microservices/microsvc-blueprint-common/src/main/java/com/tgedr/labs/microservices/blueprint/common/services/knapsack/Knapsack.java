package com.tgedr.labs.microservices.blueprint.common.services.knapsack;


import com.tgedr.labs.microservices.blueprint.model.Problem;

public interface Knapsack {

	String submitProblem(Problem problem) throws KnapsackException;

}