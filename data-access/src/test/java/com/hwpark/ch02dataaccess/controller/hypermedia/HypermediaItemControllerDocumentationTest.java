package com.hwpark.ch02dataaccess.controller.hypermedia;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.ItemRepository;

import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@WebFluxTest(controllers = HypermediaItemController.class)
@AutoConfigureRestDocs
class HypermediaItemControllerDocumentationTest {

    @Autowired
    WebTestClient client;

    @MockBean
    ItemRepository itemRepository;

    @Test
    void findOneItem() {
        when(itemRepository.findById("item-1")).thenReturn(
            Mono.just(new Item("item-1", "Alf alarm clock", "nothing I really need", 19.99)));

        client.get().uri("/hypermedia/items/item-1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .consumeWith(document("findOne-hypermedia",
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                links(linkWithRel("self").description("이 `Item` 에 대한 공식 링크"),
                    linkWithRel("item").description("`Item` 목록 링크"))));
    }

}