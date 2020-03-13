package com.tgedr.labs.microservices.blueprint.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.io.Serializable;

@Data
public class Item implements Serializable {

    @Setter(AccessLevel.NONE)
    private final Integer weight;
    @Setter(AccessLevel.NONE)
    private final Integer value;

    private Item(Integer weight, Integer value) {
        this.weight = weight;
        this.value = value;
    }
    public static Item from(Integer weight, Integer value){
        return new Item( weight,  value);
    }

}
