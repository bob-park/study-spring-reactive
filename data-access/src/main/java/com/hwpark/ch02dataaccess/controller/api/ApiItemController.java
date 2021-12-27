package com.hwpark.ch02dataaccess.controller.api;

import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.ItemRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping("api")
public class ApiItemController {

    private final ItemRepository itemRepository;

    @GetMapping(path = "items")
    public Flux<Item> findAll() {
        return itemRepository.findAll();
    }

    @GetMapping(path = "items/{id}")
    public Mono<Item> findOne(@PathVariable String id) {
        return itemRepository.findById(id);
    }

    @PostMapping(path = "items")
    // Parameter 가 Reactor 타입이므로, 구독이 발생하지 않으면, 요청 본문의 Item 타입으로 역직렬화하는 과정도 실행되지 않는다.
    public Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<Item> item) {
        return item.flatMap(
                itemRepository::save) // 인자로 받은 Item 을 Mono 에서 꺼낸후 저장하고 다시 Mono 로 반환해야하므로, flatMap 사용
            .map(savedItem -> ResponseEntity
                // 이건, Location 을 통해서 Redirect 를 해줬다는 말인가?
                // 솔직히 잘 모르겠다. 그냥 Response Header 에 Location 을 명시하는 것 뿐인가?
                // ! 알았다.
                // * https://developer.mozilla.org/ko/docs/Web/HTTP/Status/201 참고
                .created(URI.create(
                    "/api/items/" + savedItem.getId()))
                .body(savedItem));

    }

    @PutMapping(path = "items/{id}")
    public Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<Item> item,
        @PathVariable String id) {
        return item.map(content ->
                new Item(id, content.getName(), content.getDescription(), content.getPrice()))
            .flatMap(itemRepository::save)
            // tehnReturn() 메서드는 ResponseEntity.ok() 메서드를 사용해서 교체 후 데이터를 HTTP 200 OK 와 함계 반환한다.
            .map(ResponseEntity::ok);
        // ! Reactive String Data 에서 제공하는 save(), delete() 메서드를 사용하고 이후 then**() 메서드를 호출할 떄 항상, flatMap() 을 사용해야한다.
        // ! 그렇지 않으면, 저장도 삭제도 되지 않는다. flatMap() 을 사용해서 결괏값을 꺼내야 데이터 스토어에도 변경이 적용된다.
    }

}
