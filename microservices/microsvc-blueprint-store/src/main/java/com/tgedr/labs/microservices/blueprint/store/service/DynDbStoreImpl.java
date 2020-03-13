package com.tgedr.labs.microservices.blueprint.store.service;

import com.tgedr.labs.microservices.blueprint.model.Task;
import com.tgedr.labs.microservices.blueprint.store.exceptions.StoreException;
import com.tgedr.labs.microservices.blueprint.store.repository.dyndb.TaskDto2Entity;
import com.tgedr.labs.microservices.blueprint.store.repository.dyndb.TaskDynDbRepository;
import com.tgedr.labs.microservices.blueprint.store.repository.dyndb.TaskEntity2Dto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
class DynDbStoreImpl implements Store {

	private final TaskDynDbRepository repository;
	private final TaskEntity2Dto entity2Dto;
	private final TaskDto2Entity dto2Entity;

	public DynDbStoreImpl(@Autowired TaskDynDbRepository repository){
		this.repository = repository;
		this.entity2Dto = new TaskEntity2Dto();
		this.dto2Entity = new TaskDto2Entity();
	}

	@Override
	public Optional<Task> getTask(final String taskId) throws StoreException {
		log.trace("[getTask|in] ({})", taskId);
		Optional<Task> result = null;
		try {
			Optional<com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Task> r = repository.findById(taskId);
			if( r.isPresent() )
				result = Optional.of(entity2Dto.apply(r.get()));
			else
				result = Optional.empty();
		} catch (Exception e) {
			throw new StoreException(e);
		}
		log.trace("[getTask|out] => {}", result);
		return result;
	}

	@Override
	public Set<Task> getTasks() throws StoreException {
		log.trace("[getTasks|in]");
		Set<Task> result = new HashSet<>();
		try {
			for(com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Task t:repository.findAll())
				result.add(entity2Dto.apply(t));
		} catch (Exception e) {
			throw new StoreException(e);
		}
		log.trace("[getTasks|out] => {}", result);
		return result;
	}

	@Override
	public void save(Task task) throws StoreException {
		log.trace("[postTask|in] ({})", task);
		try {
			repository.save(dto2Entity.apply(task));
		} catch (Exception e) {
			throw new StoreException(e);
		}
		log.trace("[postTask|out]");
	}

}
