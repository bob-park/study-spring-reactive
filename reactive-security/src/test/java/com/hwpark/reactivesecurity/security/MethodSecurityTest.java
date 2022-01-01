package com.hwpark.reactivesecurity.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hwpark.commons.domain.Item;
import com.hwpark.reactivesecurity.repository.ItemRepository;

import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
class MethodSecurityTest {

    @Autowired
    WebTestClient client;

    @Autowired
    ItemRepository itemRepository;

    @Test
    @WithMockUser(username = "alice", roles = "SOME_OTHER_ROLE")
    void addingInventoryWithoutProperRoleFails() {

        Item item = new Item("iPhone 13 pro", "upgrade", 999.99);

        client.post().uri("/api/items/add")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(toJson(item))
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    @WithMockUser(username = "bob", roles = {"INVENTORY"})
    void addingInventoryWithProperRoleSucceeds() {

        Item item = new Item("iPhone 13 pro", "upgrade", 999.99);

        client.post().uri("/api/items/add")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(toJson(item))
            .exchange()
            .expectStatus().isCreated();

        itemRepository.findByName("iPhone 13 pro")
            .as(StepVerifier::create)
            .expectNextMatches(i -> {
                assertThat(i.getDescription()).isEqualTo("upgrade");
                assertThat(i.getPrice()).isEqualTo(999.99);

                return true;
            })
            .verifyComplete();

    }

    private String toJson(Object obj) {
        Gson gson = new GsonBuilder().create();

        return gson.toJson(obj);
    }

}
