package com.hwpark.ch02dataaccess.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;

import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.ItemRepository;

import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Service
public class InventoryService {

    private final ItemRepository itemRepository;

    public Flux<Item> searchByExample(String name, String description, boolean useAnd) {
        Item item = new Item(name, description, 0.0);

        /*
         ! ExampleMatcher 는 엄격한 타입 방식으로 구현되어 있어, Class 필드에 정보가 합치되는 몽고디비 도큐먼트에 대해서만 Example 쿼리가 적용된다.

         * 타입 검사를 우회해서 모든 컬렉션에 대해 쿼리를 수행하러면 ExampleMatcher 대신 UntypedExampleMatcher 를 사용해야 한다.
         */
        ExampleMatcher matcher =
            (useAnd ? ExampleMatcher.matchingAll() : ExampleMatcher.matchingAny())
                .withStringMatcher(StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withIgnorePaths("price");

        Example<Item> probe = Example.of(item, matcher);

        return itemRepository.findAll(probe);

    }

}
