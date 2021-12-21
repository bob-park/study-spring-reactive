package com.hwpark.ch02dataaccess.domain;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Item {

    @Id
    private String id;

    private String name;
    private double price;

    private Item() {
    }

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

}
