package com.tgedr.labs.microservices.blueprint.store.repository.dyndb;

import com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Task;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface TaskDynDbRepository extends CrudRepository<Task,String> {

}
