package com.hwpark.reactivesecurity.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.hwpark.commons.domain.User;

import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {

    Mono<User> findByName(String name);
}
