package com.hwpark.reactivesecurity.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;

import com.hwpark.commons.domain.Item;

import reactor.core.publisher.Mono;

public interface ItemRepository extends ReactiveMongoRepository<Item, String>,
    ReactiveQueryByExampleExecutor<Item> {

    Mono<Item> findByName(String name);

}
