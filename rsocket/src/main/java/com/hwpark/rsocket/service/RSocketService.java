package com.hwpark.rsocket.service;

import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Service;

import com.hwpark.rsocket.domain.Item;
import com.hwpark.rsocket.repository.ItemRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@Service
public class RSocketService {

    private final ItemRepository itemRepository;

    private final Sinks.Many<Item> itemSink;

    public RSocketService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;

        this.itemSink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @MessageMapping("newItems.request-response")
    public Mono<Item> processNewItemsViaRSocketRequestResponse(Item item) {
        return itemRepository.save(item)
            .doOnNext(itemSink::tryEmitNext);
    }

    @MessageMapping("newItems.request-stream")
    public Flux<Item> findItemsViaRSocketRequestStream() {
        return itemRepository.findAll()
            .doOnNext(itemSink::tryEmitNext);
    }

    @MessageMapping("newItem.fire-and-forget")
    public Mono<Void> processNewItemsViaRSocketFireAndForget(Item item) {
        return itemRepository.save(item)
            .doOnNext(itemSink::tryEmitNext)
            .then();
    }

    @MessageMapping("newItems.monitor")
    public Flux<Item> monitorNewItems() {
        return itemSink.asFlux();
    }
}
