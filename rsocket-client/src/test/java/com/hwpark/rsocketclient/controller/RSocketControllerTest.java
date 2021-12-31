package com.hwpark.rsocketclient.controller;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.hwpark.rsocketclient.domain.Item;
import com.hwpark.rsocketclient.repository.ItemRepository;

import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.as;
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

    @Test
    void verifyRemoteOperationsThroughRSocketRequestStream() {
        itemRepository.deleteAll().block();

        List<Item> items = IntStream.rangeClosed(1, 3)
            .mapToObj(i -> new Item("name - " + i, "description - " + i, i))
            .collect(Collectors.toList());

        itemRepository.saveAll(items).blockLast();

        client.get().uri("/items/request-stream")
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus().isOk()
            .returnResult(Item.class)
            .getResponseBody()
            .as(StepVerifier::create)
            .expectNextMatches(itemPredicate("1"))
            .expectNextMatches(itemPredicate("2"))
            .expectNextMatches(itemPredicate("3"))
            .verifyComplete();
    }

    private Predicate<Item> itemPredicate(String num) {

        return item -> {
            assertThat(item.getName()).startsWith("name");
            assertThat(item.getName()).endsWith(num);
            assertThat(item.getDescription()).startsWith("description");
            assertThat(item.getDescription()).endsWith(num);
            assertThat(item.getPrice()).isPositive();

            return true;
        };

    }

}