package com.hwpark.ch02dataaccess.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.ItemRepository;

import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
@Testcontainers
@ContextConfiguration
class RabbitTest {

    @Container
    static RabbitMQContainer container = new RabbitMQContainer("rabbitmq:3.7.25-management-alpine");

    @Autowired
    WebTestClient client;

    @Autowired
    ItemRepository itemRepository;

    /*
     Functional Interface 중 Supplier 를 사용해서 환경설정 내용을 Environment 에 동적으로 추가한다.
     */
    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", container::getContainerIpAddress);
        registry.add("spring.rabbitmq.port", container::getAmqpPort);
    }

    @Test
    void verifyMessagingThroughAmqp() throws InterruptedException {
        client.post().uri("/items")
            .bodyValue(new Item("Alf alarm clock", "nothing important", 19.99))
            .exchange()
            .expectStatus().isCreated()
            .expectBody();

        Thread.sleep(1_500);

        client.post().uri("/items")
            .bodyValue(new Item("Smurf TV tray", "nothing important", 29.99))
            .exchange()
            .expectStatus().isCreated()
            .expectBody();

        Thread.sleep(2_000);

        itemRepository.findAll()
            .as(StepVerifier::create)
            .expectNextMatches(item -> {
                assertThat(item.getName()).isEqualTo("Alf alarm clock");
                assertThat(item.getDescription()).isEqualTo("nothing important");
                assertThat(item.getPrice()).isEqualTo(19.99);

                return true;
            })
            .expectNextMatches(item -> {
                assertThat(item.getName()).isEqualTo("Smurf TV tray");
                assertThat(item.getDescription()).isEqualTo("nothing important");
                assertThat(item.getPrice()).isEqualTo(29.99);

                return true;
            })
            .verifyComplete();

    }

}
