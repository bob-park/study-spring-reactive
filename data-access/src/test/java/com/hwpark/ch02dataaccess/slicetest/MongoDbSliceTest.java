package com.hwpark.ch02dataaccess.slicetest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.ItemRepository;

import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

// Spring data mongoDB 활용에 초점을 둔 테스트 관련 기능 활성화
// Spring Container 가 구동되지만, MongoDB 관련 Bean 을 제외한 다른 Bean 정의 무시한다.
@DataMongoTest
class MongoDbSliceTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    void itemRepositorySavesItems() {
        Item sampleItem = new Item("name", "description", 1.99);

        itemRepository.save(sampleItem)
            .as(StepVerifier::create)
            .expectNextMatches(item -> {
                assertThat(item.getId()).isNotNull();
                assertThat(item.getName()).isEqualTo("name");
                assertThat(item.getDescription()).isEqualTo("description");
                assertThat(item.getPrice()).isEqualTo(1.99);

                return true;
            })
            .verifyComplete();
    }

}
