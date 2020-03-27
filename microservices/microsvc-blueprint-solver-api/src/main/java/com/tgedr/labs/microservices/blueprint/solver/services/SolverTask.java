package com.tgedr.labs.microservices.blueprint.solver.services;

import lombok.extern.slf4j.Slf4j;
import com.tgedr.labs.microservices.blueprint.services.statemanager.StateManager;
import com.tgedr.labs.microservices.blueprint.model.Item;
import com.tgedr.labs.microservices.blueprint.model.Solution;
import com.tgedr.labs.microservices.blueprint.model.Status;
import com.tgedr.labs.microservices.blueprint.model.Task;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SolverTask implements Runnable {

	private static final long MILLI = 1000;

	private final StateManager stateManager;
	private final Task task;

	public SolverTask(final StateManager taskStateManager, final Task task) {
		log.trace("[SolverTask|in] task: {}", task);
		this.stateManager = taskStateManager;
		this.task = task;
		log.trace("[SolverTask|out]");
	}

	@Override
	public void run() {
		log.trace("[run|in]");
		try {
			Item[] items = task.getProblem().getItems().toArray(new Item[]{});
			int capacity = task.getProblem().getCapacity();

			// start solving it
			final long start = System.currentTimeMillis() / MILLI;
			task.getStatuses().put(Status.started, start);
			this.stateManager.setState(task);
			log.info("[run] solver started task {} on {}", task.getId(), start);
			List<Item> selectedItems = solve(items, capacity);

			// collect end data and solution
			final long end = System.currentTimeMillis() / MILLI;
			log.info("[run] solver ended task {} on {}", task.getId(), end);
			final int time = (int) (end - start);
			task.setSolution(new Solution(selectedItems, time));
			log.info("[run] task {} took {}s and solution is: {}", task.getId(), time, task.getSolution());
			task.getStatuses().put(Status.completed, end);
			this.stateManager.setState(task);
		} catch (Exception e) {
			log.error("[run] solver failed on task {}", task.getId(), e);
			try {
				task.getStatuses().put(Status.failed, System.currentTimeMillis() / MILLI);
				this.stateManager.setState(task);
			} catch (Exception ex) { log.error("[run] was trying to set failed state", ex); }
		} finally {
			log.trace("[run|out]");
		}
	}

	List<Item> solve(final Item[] items, final int capacity) {
		log.trace("[solve|in] ({}, {})", items, capacity);

		List<Item> result = new ArrayList<>();

		// we use a matrix to store the max value at each n-th item
		int[][] matrix = new int[items.length + 1][capacity + 1];

		// first line is initialized to 0
		for (int i = 0; i <= capacity; i++)
			matrix[0][i] = 0;

		// we iterate on items
		for (int i = 1; i <= items.length; i++) {
			// we iterate on each capacity
			for (int j = 0; j <= capacity; j++) {
				if (items[i - 1].getWeight() > j)
					matrix[i][j] = matrix[i-1][j];
				else
					// we maximize value at this rank in the matrix
					matrix[i][j] = Math.max(matrix[i-1][j], matrix[i-1][j - items[i-1].getWeight()]
							+ items[i-1].getValue());
			}
		}

		int res = matrix[items.length][capacity];
		int w = capacity;

		for (int i = items.length; i > 0  &&  res > 0; i--) {
			if (res != matrix[i-1][w]) {
				result.add(items[i-1]);
				// we remove items value and weight
				res -= items[i-1].getValue();
				w -= items[i-1].getWeight();
			}
		}

		log.trace("[solve|out] => {}", result);
		return result;
	}

}
