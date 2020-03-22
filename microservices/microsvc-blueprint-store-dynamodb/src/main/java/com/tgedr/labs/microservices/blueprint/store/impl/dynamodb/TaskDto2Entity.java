package com.tgedr.labs.microservices.blueprint.store.impl.dynamodb;

import com.tgedr.labs.microservices.blueprint.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class TaskDto2Entity implements Function <com.tgedr.labs.microservices.blueprint.model.Task, Task> {
    @Override
    public Task apply(com.tgedr.labs.microservices.blueprint.model.Task task) {
        Task entity = new Task();

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

    Solution toEntity(com.tgedr.labs.microservices.blueprint.model.Solution s){
        Solution entity = new Solution();
        entity.setTime(s.getTime());
        List<Item> items = new ArrayList<>();
        entity.setItems(items);
        if( null != s.getItems() ) {
            for (com.tgedr.labs.microservices.blueprint.model.Item i : s.getItems())
                items.add(toEntity(i));
        }
        return entity;
    }

    Problem toEntity(com.tgedr.labs.microservices.blueprint.model.Problem p){
        Problem entity = new Problem();
        entity.setCapacity(p.getCapacity());
        List<Item> items = new ArrayList<>();
        entity.setItems(items);

        if( null != p.getItems() ) {
            for (com.tgedr.labs.microservices.blueprint.model.Item i : p.getItems())
                items.add(toEntity(i));
        }
        return entity;
    }

    Item toEntity(com.tgedr.labs.microservices.blueprint.model.Item i){
        Item entity = new Item();
        entity.setValue(i.getValue());
        entity.setWeight(i.getWeight());
        return entity;
    }

}
