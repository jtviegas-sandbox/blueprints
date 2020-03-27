package com.tgedr.labs.microservices.blueprint.services.statemanager;

import com.tgedr.labs.microservices.blueprint.model.Status;
import com.tgedr.labs.microservices.blueprint.model.Task;
import com.tgedr.labs.microservices.blueprint.services.statemanager.exceptions.StateManagerException;

import java.util.Map;
import java.util.Optional;

public interface StateManager {

	void setState(Task state) throws StateManagerException;
	// status x timestamp
	Map<Status,Long> getStatus(String id) throws StateManagerException;
	// id x status x timestamp
	Map<String,Map<Status,Long>> getStatuses() throws StateManagerException;
	Optional<Task> getState(String id) throws StateManagerException;

}
