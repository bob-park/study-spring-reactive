package com.hwpark.ch02dataaccess.commons.loader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import com.hwpark.ch02dataaccess.domain.Item;

/**
 * MongoDB 를 사용할 떄, 데이터 로딩시 Blocking 이 아닌 Non-Blocking 을 사용할 땐, 다음과 같이 ReactiveMongoTemplate 인
 * MongoOperations 를 사용하면 된다.
 * <p>
 * 사용법은 ReactiveMongoRepository 와 동일하다.
 */
@Component
public class TemplateDataLoader {

    /**
     * MongoOperations 은 Spring 에서 사용하는 JdbcTemplate 에서 JdbcOperation 을 분리와 동일
     * <p>
     * 즉, Application 과 DB 간의 결합도를 낮춘것
     *
     * @param mongo
     * @return
     */
    @Bean
    public CommandLineRunner initialize(MongoOperations mongo) {
        return args -> {
            mongo.save(new Item("Alf alarm clock", 19.99));
            mongo.save(new Item("Smurf TV tray", 124.99));
        };
    }

}
