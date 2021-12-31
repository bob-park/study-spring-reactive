package com.hwpark.rsocketclient.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.hwpark.rsocketclient.domain.Item;
import com.hwpark.rsocketclient.repository.ItemRepository;

import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
class RSocketControllerTest {

    @Autowired
    WebTestClient client;

    @Autowired
    ItemRepository itemRepository;

    @Test
    void verifyRemoteOperationsThroughRSocketRequestResponse() throws InterruptedException {
        itemRepository.deleteAll()
            .as(StepVerifier::create)
            .verifyComplete();

        client.post().uri("/items/request-response")
            .bodyValue(new Item("Alf alarm clock", "nothing important", 19.99))
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Item.class)
            .value(item -> {
                assertThat(item.getId()).isNotNull();
                assertThat(item.getName()).isEqualTo("Alf alarm clock");
                assertThat(item.getDescription()).isEqualTo("nothing important");
                assertThat(item.getPrice()).isEqualTo(19.99);
            });

        Thread.sleep(500);

        itemRepository.findAll()
            .as(StepVerifier::create)
            .expectNextMatches(item -> {
                assertThat(item.getId()).isNotNull();
                assertThat(item.getName()).isEqualTo("Alf alarm clock");
                assertThat(item.getDescription()).isEqualTo("nothing important");
                assertThat(item.getPrice()).isEqualTo(19.99);

                return true;
            })
            .verifyComplete();
    }

}