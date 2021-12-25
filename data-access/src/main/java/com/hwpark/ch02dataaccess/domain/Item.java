package com.hwpark.ch02dataaccess.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public Item(String name, String description, String distributorRegion, double price) {
        this.name = name;
        this.description = description;
        this.distributorRegion = distributorRegion;
        this.price = price;
    }
}
