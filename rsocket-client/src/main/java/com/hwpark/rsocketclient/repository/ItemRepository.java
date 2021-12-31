package com.hwpark.rsocketclient.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.hwpark.rsocketclient.domain.Item;

public interface ItemRepository extends ReactiveMongoRepository<Item, String> {

}
