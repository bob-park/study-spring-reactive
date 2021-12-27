package com.hwpark.ch02dataaccess;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@code @SpringBootTest} Spring Container 가 실제 구동됨
 * <pre>
 *      - WebEnvironment.RANDOM_PORT : 테스트할 때 임의의 포트에 내장 컨테이너가 바인딩 된다.
 * </pre>
 */
//@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
//@AutoConfigureWebClient // Application 에 요청을 날리는 WebTestClient 인스턴스를 생성
class LoadingWebSiteIntegrationTest {

    @Autowired
    WebTestClient client; // WebTestClient 주입

//    @Test
    void test() {

        /*
          - WebTestClient 를 사용해서 HomeController 경로를 호출
          - WebTestClient 를 통해 다음을 검증한다.
              - Response Header
                    - Content-Type
              - Response Body
         */
        client.get().uri("/").exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.TEXT_HTML)
            .expectBody(String.class)
            .consumeWith(exchangeResult -> {
                assertThat(exchangeResult.getResponseBody()).contains("<table");
            });

    }

}
