package com.hwpark.ch02dataaccess.controller.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hwpark.ch02dataaccess.domain.Item;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RequiredArgsConstructor
@RestController
public class SpringAmqpItemController {

    private final AmqpTemplate template;

    @PostMapping(path = "items")
    public Mono<ResponseEntity<?>> addNewItemUsingSpringAmqp(@RequestBody Mono<Item> item) {
        return item
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(content -> Mono.fromCallable(() -> {
                template.convertAndSend("hacking-spring-boot", "new-items-spring-amqp", content);
                return ResponseEntity.created(URI.create("/items")).build();
            }));
    }

}
