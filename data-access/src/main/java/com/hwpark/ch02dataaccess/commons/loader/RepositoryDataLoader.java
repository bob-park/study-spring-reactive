package com.hwpark.ch02dataaccess.commons.loader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.BlockingItemRepository;

/**
 * 이것은 Blocking Repository 를 사용한 Data loading
 */
//@Component
public class RepositoryDataLoader {

    /**
     * CommandLineRunner 는 application 이 시작된 후 자동으로 실행되는 특수한 Spring Boot Component
     * <p>
     * application 에서 사용되는 모든 Component 가 등록되고 활성화된 이후 run() 이 자동으로 실행되는 것이 보장된다.
     *
     * @param repository
     * @return
     */
    @Bean
    public CommandLineRunner initialize(BlockingItemRepository repository) {
        return args -> {
            repository.save(new Item("Alf alarm clock", 19.99));
            repository.save(new Item("Smurf TV tray", 24.99));
        };
    }

}
