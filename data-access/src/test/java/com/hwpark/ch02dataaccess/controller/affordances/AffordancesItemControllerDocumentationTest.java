package com.hwpark.ch02dataaccess.controller.affordances;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.ItemRepository;

import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@WebFluxTest(controllers = AffordancesItemController.class)
@AutoConfigureRestDocs
class AffordancesItemControllerDocumentationTest {

    @Autowired
    WebTestClient client;

    @MockBean
    ItemRepository itemRepository;

    @Test
    void findSingleItemAffordances() {
        when(itemRepository.findById("item-1"))
            .thenReturn(
                Mono.just(new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99)));

        client.get().uri("/affordances/items/item-1")
            .accept(MediaTypes.HAL_FORMS_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .consumeWith(document("single-item-affordances",
                preprocessResponse(prettyPrint())));
    }

}