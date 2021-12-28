package com.hwpark.ch02dataaccess.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.ItemRepository;

import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class SpringAmqpItemService {

    private final ItemRepository itemRepository;

    @RabbitListener(ackMode = "MANUAL", bindings = @QueueBinding(value = @Queue, exchange = @Exchange("hacking-spring-boot"), key = "new-items-spring-amqp"))
    public Mono<Void> processNewItemsViaSpringAmqp(Item item) {
        log.debug("Consuming -> {}", item);
        return itemRepository.save(item).then();
    }

}
