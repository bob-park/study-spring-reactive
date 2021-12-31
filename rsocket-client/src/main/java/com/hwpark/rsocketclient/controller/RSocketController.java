package com.hwpark.rsocketclient.controller;

import java.net.URI;

import org.reactivestreams.Publisher;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hwpark.rsocketclient.domain.Item;

import reactor.core.publisher.Mono;

import static io.rsocket.metadata.WellKnownMimeType.MESSAGE_RSOCKET_ROUTING;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.parseMediaType;

@RestController
public class RSocketController {

    private final RSocketRequester rSocketRequester;

    public RSocketController() {

        this.rSocketRequester = RSocketRequester.builder()
            // ! 꼭 decoder 와 encoder 를 추가해줘야 되나봄
            .rsocketStrategies(builder -> builder
                .decoder(new Jackson2JsonDecoder())
                .encoder(new Jackson2JsonEncoder()))
            .dataMimeType(APPLICATION_JSON)
            .metadataMimeType(parseMediaType(MESSAGE_RSOCKET_ROUTING.toString()))
            .tcp("localhost", 17_000);
    }

    @PostMapping("/items/request-response")
    public Mono<ResponseEntity<?>> addNewItemUsingRSocketRequestResponse(
        @RequestBody Item item) {

        return rSocketRequester
            .route("newItems.request-response")
            .data(item)
            .retrieveMono(Item.class)
            .map(savedItem -> ResponseEntity.created(URI.create("/items/request-response"))
                .body(savedItem));

    }

}
