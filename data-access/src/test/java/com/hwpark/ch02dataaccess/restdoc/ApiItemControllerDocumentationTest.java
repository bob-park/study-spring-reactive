package com.hwpark.ch02dataaccess.restdoc;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.hwpark.ch02dataaccess.controller.api.ApiItemController;
import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.ItemRepository;
import com.hwpark.ch02dataaccess.service.InventoryService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@WebFluxTest(controllers = ApiItemController.class) // WebFlux Controller 테스트에 필요한 내용만 자동 설정
@AutoConfigureRestDocs // Spring Rest Doc 사용에 필요한 내용을 자동으로 설정해준다.
class ApiItemControllerDocumentationTest {

    @Autowired
    WebTestClient client;

    @MockBean
    InventoryService inventoryService;

    @MockBean
    ItemRepository itemRepository;

    @Test
    void findingAllItems() {
        when(itemRepository.findAll()).thenReturn(Flux.just(
            new Item("item-1", "Alf alram clock", "nothing I really need", 19.99)
        ));

        client.get().uri("/api/items")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            // document() 는 Spring Rest Doc 정적 메서드
            // 문서 생성 기능을 테스트에 추가하는 역할
            // 문서는 설정파일에 따라 생성된다.
            .consumeWith(document("findAll", preprocessResponse(prettyPrint())));
    }

    @Test
    void postNewItem() {
        when(itemRepository.save(any()))
            .thenReturn(
                Mono.just(
                    new Item("1", "Alf alarm clock", "nothing important", 19.99)));

        client.post().uri("/api/items")
            .bodyValue(new Item("Alf alarm clock", "nothing important", 19.99))
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .consumeWith(document("post-new-item",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint())));
    }

}
