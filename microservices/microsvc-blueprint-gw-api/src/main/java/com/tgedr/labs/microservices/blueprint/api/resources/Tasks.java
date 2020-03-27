package com.tgedr.labs.microservices.blueprint.api.resources;

import com.tgedr.labs.microservices.blueprint.common.exceptions.ApiException;
import com.tgedr.labs.microservices.blueprint.services.statemanager.exceptions.StateManagerException;
import com.tgedr.labs.microservices.blueprint.services.knapsack.Knapsack;
import com.tgedr.labs.microservices.blueprint.services.knapsack.exceptions.KnapsackException;
import com.tgedr.labs.microservices.blueprint.services.statemanager.StateManager;
import com.tgedr.labs.microservices.blueprint.model.Problem;
import com.tgedr.labs.microservices.blueprint.model.Status;
import com.tgedr.labs.microservices.blueprint.model.Task;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/tasks")
@Api(tags = { "tasks api" }, value = "API root for knapsack tasks")
@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid status value", response = void.class),
		@ApiResponse(code = 500, message = "Internal server error", response = void.class) })
@Slf4j
public class Tasks {

	private static final long MILLI = 1000;

	private final StateManager stateManager;
	private final Knapsack knapsack;

	public Tasks(final @Autowired StateManager stateManager, final @Autowired Knapsack knapsack) {
		this.stateManager = stateManager;
		this.knapsack = knapsack;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "Used to post a knapsack problem and create a task, receiving its id", notes = "", response = String.class)
	@io.swagger.annotations.ApiResponses(value = {
			@ApiResponse(code = 200, message = "successful operation", response = String.class) })
	public ResponseEntity<String> postProblem(@RequestBody @Valid Problem problem) throws ApiException {
		log.info("[postProblem|in] problem: {}", problem);
		try {
			String id = knapsack.submitProblem(problem);
			return new ResponseEntity<String>(id, HttpStatus.CREATED);
		} catch (KnapsackException e) {
			log.error("[postProblem] something wrong when trying to post a problem", e);
			throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[postProblem|out]");
		}
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "Used to get a task related to a previously submitted problem", response = Task.class)
	@io.swagger.annotations.ApiResponses(value = {
			@ApiResponse(code = 200, message = "successful operation", response = Task.class),
			@ApiResponse(code = 404, message = "no match", response = void.class) })
	public ResponseEntity<Task> getTask(@ApiParam @PathVariable("id") String ident) throws ApiException {
		log.info("[getTask|in] ident: {}", ident);
		Task task = null;
		try {
			Optional<Task> wrapper = stateManager.getState(ident);
			if (wrapper.isPresent())
				task = wrapper.get();
			else
				throw new ApiException(String.format("no task found with id %s", ident), HttpStatus.NOT_FOUND);

			return new ResponseEntity<Task>(task, HttpStatus.OK);
		} catch (StateManagerException e) {
			log.error("[getTask] something wrong when trying to get a task", e);
			throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[getTask|out] {}", task);
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	@ApiOperation(value = "Used to get a list of status related to submitted knapsack tasks", notes = "", response = Map.class)
	@io.swagger.annotations.ApiResponses(value = {
			@ApiResponse(code = 200, message = "successful operation", response = Map.class) })
	public ResponseEntity<Map<String, Map<Status, Long>>> getStatuses() throws ApiException {
		log.info("[getStatuses|in]");
		Map<String, Map<Status, Long>> result = null;
		try {
			result = stateManager.getStatuses();
			return new ResponseEntity<Map<String, Map<Status, Long>>>(result, HttpStatus.OK);
		} catch (Exception e) {
			log.error("[getStatuses]", e);
			throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[getStatuses|out] {}", result);
		}
	}

}
