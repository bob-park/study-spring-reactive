package com.hwpark.commons.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Item {

    @Id
    private String id;

    private String name;
    private String description;
    private double price;
    private String distributorRegion;
    private LocalDateTime releaseDate;
    private int availableUnits;
    private Point location;
    private boolean active;

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Item(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Item(String id, String name, String description, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
}
