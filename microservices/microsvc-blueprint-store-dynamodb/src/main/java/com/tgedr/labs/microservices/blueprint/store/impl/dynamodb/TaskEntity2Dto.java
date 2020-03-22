package com.tgedr.labs.microservices.blueprint.store.impl.dynamodb;

import com.tgedr.labs.microservices.blueprint.model.Status;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

class TaskEntity2Dto implements Function <Task,com.tgedr.labs.microservices.blueprint.model.Task> {
    @Override
    public com.tgedr.labs.microservices.blueprint.model.Task apply(Task task) {

        com.tgedr.labs.microservices.blueprint.model.Problem p = null;
        if( null != task.getProblem() )
            p = toDto(task.getProblem() );

        com.tgedr.labs.microservices.blueprint.model.Task dto = com.tgedr.labs.microservices.blueprint.model.Task.from(task.getId(), p);
        if( null != task.getSolution() )
            dto.setSolution(toDto(task.getSolution()));

        if( null != task.getStatuses() && (!task.getStatuses().isEmpty()) ){
            for( Map.Entry<Status,Long> entry: task.getStatuses().entrySet() )
                dto.getStatuses().put(entry.getKey(), entry.getValue());
        }

        return dto;
    }

    public com.tgedr.labs.microservices.blueprint.model.Problem toDto(Problem p) {
        List<com.tgedr.labs.microservices.blueprint.model.Item> items = new ArrayList<>();
        if( null != p.getItems() ) {
            for (Item i : p.getItems())
                items.add(toDto(i));
        }
        return new com.tgedr.labs.microservices.blueprint.model.Problem(p.getCapacity(), items);
    }

    com.tgedr.labs.microservices.blueprint.model.Item toDto(Item i){
        return com.tgedr.labs.microservices.blueprint.model.Item.from(i.getWeight(), i.getValue());
    }

    com.tgedr.labs.microservices.blueprint.model.Solution toDto(Solution s){
        List<com.tgedr.labs.microservices.blueprint.model.Item> items = new ArrayList<>();
        if( null != s.getItems()){
            for (Item i : s.getItems())
                items.add(toDto(i));
        }
        return new com.tgedr.labs.microservices.blueprint.model.Solution(items, s.getTime());
    }

}
