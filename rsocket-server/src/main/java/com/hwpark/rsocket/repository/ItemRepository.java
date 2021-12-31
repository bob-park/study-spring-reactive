package com.hwpark.rsocket.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;

import com.hwpark.rsocket.domain.Item;

public interface ItemRepository extends ReactiveMongoRepository<Item, String>,
    ReactiveQueryByExampleExecutor<Item> {

}
