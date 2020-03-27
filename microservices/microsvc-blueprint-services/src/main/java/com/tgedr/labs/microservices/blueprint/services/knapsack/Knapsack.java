package com.tgedr.labs.microservices.blueprint.services.knapsack;


import com.tgedr.labs.microservices.blueprint.model.Problem;
import com.tgedr.labs.microservices.blueprint.services.knapsack.exceptions.KnapsackException;

public interface Knapsack {

	String submitProblem(Problem problem) throws KnapsackException;

}