package com.hwpark.reactivesecurity.controller.api;

import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hwpark.commons.domain.Item;
import com.hwpark.reactivesecurity.repository.ItemRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("api")
public class ApiItemController {

    private final ItemRepository itemRepository;

    @GetMapping(path = "items")
    public Flux<Item> findAll() {
        return itemRepository.findAll();
    }

    @GetMapping(path = "items/{id}")
    public Mono<Item> findOne(@PathVariable String id) {
        return itemRepository.findById(id);
    }

    @PostMapping(path = "items")
    public Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<Item> item) {
        return item.flatMap(itemRepository::save)
            .map(savedItem -> ResponseEntity
                .created(URI.create(
                    "/api/items/" + savedItem.getId()))
                .body(savedItem));

    }

    @PutMapping(path = "items/{id}")
    public Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<Item> item,
        @PathVariable String id) {
        return item.map(content ->
                new Item(id, content.getName(), content.getDescription(), content.getPrice()))
            .flatMap(itemRepository::save)
            .map(ResponseEntity::ok);
    }

}
