package com.tgedr.labs.microservices.blueprint.store.entity.dyndb;

import lombok.Data;

@Data
public class Item {

    private Integer weight;
    private Integer value;

    public static Item from(Integer value, Integer weight){
        Item r = new Item();
        r.setValue(value);
        r.setWeight(weight);
        return r;
    }

}
