package com.tgedr.labs.microservices.blueprint.store.repository.dyndb;

import com.tgedr.labs.microservices.blueprint.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TaskDto2Entity implements Function <Task, com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Task> {
    @Override
    public com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Task apply(Task task) {
        com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Task entity = new com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Task();

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

    com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Solution toEntity(Solution s){
        com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Solution entity = new com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Solution();
        entity.setTime(s.getTime());
        List<com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Item> items = new ArrayList<>();
        entity.setItems(items);
        if( null != s.getItems() ) {
            for (Item i : s.getItems())
                items.add(toEntity(i));
        }
        return entity;
    }

    com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Problem toEntity(Problem p){
        com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Problem entity = new com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Problem();
        entity.setCapacity(p.getCapacity());
        List<com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Item> items = new ArrayList<>();
        entity.setItems(items);

        if( null != p.getItems() ) {
            for (Item i : p.getItems())
                items.add(toEntity(i));
        }
        return entity;
    }

    com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Item toEntity(Item i){
        com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Item entity = new com.tgedr.labs.microservices.blueprint.store.entity.dyndb.Item();
        entity.setValue(i.getValue());
        entity.setWeight(i.getWeight());
        return entity;
    }

}
