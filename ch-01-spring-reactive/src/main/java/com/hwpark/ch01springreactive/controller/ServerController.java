package com.hwpark.ch01springreactive.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hwpark.ch01springreactive.domain.Dish;
import com.hwpark.ch01springreactive.service.KitchenService;

import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
public class ServerController {

    private final KitchenService kitchenService;

    @GetMapping(path = "server", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Dish> serveDishes() {
        return kitchenService.getDishes();
    }

    @GetMapping(path = "/served-dishes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Dish> deliverDishes() {
        return kitchenService.getDishes()
            .map(Dish::deliver);
    }

}
