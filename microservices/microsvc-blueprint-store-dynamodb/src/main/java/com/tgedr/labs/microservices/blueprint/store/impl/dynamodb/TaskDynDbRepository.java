package com.tgedr.labs.microservices.blueprint.store.impl.dynamodb;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
interface TaskDynDbRepository extends CrudRepository<Task,String> {

}
