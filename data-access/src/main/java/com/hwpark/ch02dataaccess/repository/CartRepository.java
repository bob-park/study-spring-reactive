package com.hwpark.ch02dataaccess.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.hwpark.ch02dataaccess.domain.Cart;

public interface CartRepository extends ReactiveMongoRepository<Cart, String> {

}
