package com.hwpark.reactivesecurity.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.hwpark.commons.domain.Cart;

public interface CartRepository extends ReactiveMongoRepository<Cart, String> {

}
