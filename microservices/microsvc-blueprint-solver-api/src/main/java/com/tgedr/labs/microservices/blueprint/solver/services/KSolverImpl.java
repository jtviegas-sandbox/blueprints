package com.tgedr.labs.microservices.blueprint.solver.services;

import com.tgedr.labs.microservices.blueprint.common.services.state.StateManager;
import lombok.extern.slf4j.Slf4j;
import com.tgedr.labs.microservices.blueprint.model.Problem;
import com.tgedr.labs.microservices.blueprint.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.lang.String.format;

@Service
@Slf4j
class KSolverImpl implements KSolver {

	private final StateManager stateManager;
	private final ThreadPoolTaskExecutor executor;

	@Override
	public String solve(Problem problem) {
		log.trace("[solve|in] problem: {}", problem);
		String id = customUuid();
		this.executor.submit(new SolverTask(stateManager, Task.from(id, problem)));
		log.trace("[solve|out] => {}", id);
		return id;
	}

	public KSolverImpl(@Autowired ThreadPoolTaskExecutor threadPoolTaskExecutor, @Autowired StateManager stateManager) {
		log.trace("[KSolverImpl|in]");
		this.stateManager = stateManager;
		this.executor = threadPoolTaskExecutor;
		log.trace("[KSolverImpl|out]");
	}

	private String customUuid(){
		log.trace("[customUuid|in]");
		String result = format("%s|%s", System.getenv("HOSTNAME"), UUID.randomUUID().toString());
		log.trace("[customUuid|out] => {}", result);
		return result;
	}

}
