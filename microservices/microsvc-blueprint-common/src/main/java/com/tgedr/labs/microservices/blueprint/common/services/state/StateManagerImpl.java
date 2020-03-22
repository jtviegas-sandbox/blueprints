package com.tgedr.labs.microservices.blueprint.common.services.state;

import com.tgedr.labs.microservices.blueprint.common.exceptions.StateManagerException;
import com.tgedr.labs.microservices.blueprint.model.Status;
import com.tgedr.labs.microservices.blueprint.model.Task;
import com.tgedr.labs.microservices.blueprint.store.exceptions.StoreException;
import com.tgedr.labs.microservices.blueprint.store.Store;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
class StateManagerImpl implements StateManager {

	private final Store store;

	public StateManagerImpl(@Autowired Store store){
		this.store = store;
	}

	@Override
	public void setState(final Task state) throws StateManagerException {
		log.debug("[setState|in] ({})", state);
		try {
			store.save(state);
		} catch (StoreException e) {
			throw new StateManagerException(e);
		}
		log.debug("[setState|out]");
	}

	@Override
	public Map<Status, Long> getStatus(final String id) throws StateManagerException {
		log.debug("[getStatus|in] ({})", id);
		Map<Status, Long> result = new HashMap<>();
		try {
			Optional<Task> response = getState(id);
			if( response.isPresent() )
				result.putAll(response.get().getStatuses());
		} catch (StateManagerException e) {
			throw new StateManagerException(e);
		}
		log.debug("[getStatus|out] => {}", result);
		return result;
	}

	@Override
	public Optional<Task> getState(final String id) throws StateManagerException {
		log.debug("[getState|in] ({})", id);
		Optional<Task> result = null;
		try {
			result = store.getTask(id);
		} catch (StoreException e) {
			throw new StateManagerException(e);
		}
		log.debug("[getState|out] => {}", result);
		return result;
	}

	@Override
	public Map<String, Map<Status, Long>> getStatuses() throws StateManagerException {
		log.debug("[getStatuses|in]");
		Map<String, Map<Status, Long>> result = new HashMap<>();

		try {
			Set<Task> tasks =store.getTasks();
			for( Task task: tasks)
				result.put(task.getId(), task.getStatuses());
		} catch (StoreException e) {
			throw new StateManagerException(e);
		}

		log.debug("[getStatuses|out] => {}", result);
		return result;
	}


}
