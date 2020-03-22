package com.tgedr.labs.microservices.blueprint.store;

import com.tgedr.labs.microservices.blueprint.model.Task;
import com.tgedr.labs.microservices.blueprint.store.exceptions.StoreException;

import java.util.Optional;
import java.util.Set;

public interface Store {

	Optional<Task> getTask(String taskId) throws StoreException;

	Set<Task> getTasks() throws StoreException;

	void save(Task task) throws StoreException;

}
