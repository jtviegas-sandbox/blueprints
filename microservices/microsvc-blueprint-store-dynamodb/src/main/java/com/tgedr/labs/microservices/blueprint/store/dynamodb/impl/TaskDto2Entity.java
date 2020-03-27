package com.tgedr.labs.microservices.blueprint.store.dynamodb.impl;

import com.tgedr.labs.microservices.blueprint.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class TaskDto2Entity implements Function <com.tgedr.labs.microservices.blueprint.model.Task, com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Task> {
    @Override
    public com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Task apply(com.tgedr.labs.microservices.blueprint.model.Task task) {
        com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Task entity = new com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Task();

        entity.setId(task.getId());
        if( null != task.getProblem() )
            entity.setProblem(toEntity(task.getProblem()));
        if( null != task.getSolution() )
            entity.setSolution(toEntity(task.getSolution()));
        Map<Status,Long> statuses = new HashMap<>();
        entity.setStatuses(statuses);
        if( null != task.getStatuses() && (!task.getStatuses().isEmpty()) ){
            for( Map.Entry<Status,Long> entry: task.getStatuses().entrySet() )
                statuses.put(entry.getKey(), entry.getValue());
        }

        return entity;
    }

    com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Solution toEntity(com.tgedr.labs.microservices.blueprint.model.Solution s){
        com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Solution entity = new com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Solution();
        entity.setTime(s.getTime());
        List<com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Item> items = new ArrayList<>();
        entity.setItems(items);
        if( null != s.getItems() ) {
            for (com.tgedr.labs.microservices.blueprint.model.Item i : s.getItems())
                items.add(toEntity(i));
        }
        return entity;
    }

    com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Problem toEntity(com.tgedr.labs.microservices.blueprint.model.Problem p){
        com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Problem entity = new com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Problem();
        entity.setCapacity(p.getCapacity());
        List<com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Item> items = new ArrayList<>();
        entity.setItems(items);

        if( null != p.getItems() ) {
            for (com.tgedr.labs.microservices.blueprint.model.Item i : p.getItems())
                items.add(toEntity(i));
        }
        return entity;
    }

    com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Item toEntity(com.tgedr.labs.microservices.blueprint.model.Item i){
        com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Item entity = new com.tgedr.labs.microservices.blueprint.store.dynamodb.entities.Item();
        entity.setValue(i.getValue());
        entity.setWeight(i.getWeight());
        return entity;
    }

}
