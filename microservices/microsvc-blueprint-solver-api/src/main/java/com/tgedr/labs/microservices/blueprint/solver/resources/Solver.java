package com.tgedr.labs.microservices.blueprint.solver.resources;

import com.tgedr.labs.microservices.blueprint.solver.services.KSolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import com.tgedr.labs.microservices.blueprint.common.exceptions.ApiException;

import com.tgedr.labs.microservices.blueprint.model.Problem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@Slf4j
@RestController
@RequestMapping(value = "/api/problem")
@Api(tags = { "knapsack problem solver api" }, value = "API root for knapsack problem solver")
@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid status value", response = void.class),
		@ApiResponse(code = 500, message = "Internal server error", response = void.class) })
public class Solver {

	private final KSolver solver;

	public Solver(final @Autowired KSolver solver) {
		this.solver = solver;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ApiOperation(value = "Used to post a knapsack problem", notes = "", response = Void.class)
	@io.swagger.annotations.ApiResponses(value = {@ApiResponse(code = 200, message = "successful operation", response = Void.class) })
	public ResponseEntity<String> postProblem(@RequestBody @Valid Problem problem) throws ApiException {
		log.info("[postProblem|in] ({})", problem);
		String id = null;
		try {
			id = solver.solve(problem);
			log.info("[postProblem] submitted task id: {} to solver", id);
			return new ResponseEntity<String>(id, HttpStatus.OK);
		} catch (Exception e) { throw new ApiException(e, HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			log.info("[postProblem|out]");
		}
	}

}
