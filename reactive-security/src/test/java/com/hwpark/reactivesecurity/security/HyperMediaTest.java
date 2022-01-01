package com.hwpark.reactivesecurity.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.HypermediaWebTestClientConfigurer;
import org.springframework.hateoas.server.core.TypeReferences.CollectionModelType;
import org.springframework.hateoas.server.core.TypeReferences.EntityModelType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.hwpark.commons.domain.Item;
import com.hwpark.reactivesecurity.repository.ItemRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType.HAL;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@EnableHypermediaSupport(type = HAL)
@AutoConfigureWebClient
class HyperMediaTest {

    @Autowired
    WebTestClient client;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    HypermediaWebTestClientConfigurer webclientConfigurer;

    @BeforeEach
    void setUp() {
        this.client = this.client.mutateWith(webclientConfigurer);
    }

    @Test
    @WithMockUser(username = "alice", roles = "INVENTORY")
    void navigateToItemWithInventoryAuthority() {
        RepresentationModel<?> root = client.get().uri("/api")
            .exchange()
            .expectBody(RepresentationModel.class)
            .returnResult().getResponseBody();

        CollectionModel<EntityModel<Item>> items = this.client.get()
            .uri(root.getRequiredLink(IanaLinkRelations.ITEM).toUri())
            .exchange()
            .expectBody(new CollectionModelType<EntityModel<Item>>() {
            })
            .returnResult().getResponseBody();

        assertThat(items.getLinks()).hasSize(2);
        assertThat(items.hasLink(IanaLinkRelations.SELF)).isTrue();
        assertThat(items.hasLink("add")).isTrue();

        EntityModel<Item> first = items.getContent().iterator().next();

        EntityModel<Item> item = client.get()
            .uri(first.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .exchange()
            .expectBody(new EntityModelType<Item>() {
            })
            .returnResult().getResponseBody();

        assertThat(item.getLinks()).hasSize(3);
        assertThat(item.hasLink(IanaLinkRelations.SELF)).isTrue();
        assertThat(item.hasLink(IanaLinkRelations.ITEM)).isTrue();
        assertThat(item.hasLink("delete")).isTrue();
    }

}
