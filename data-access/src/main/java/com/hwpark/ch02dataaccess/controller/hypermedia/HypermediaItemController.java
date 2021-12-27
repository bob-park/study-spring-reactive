package com.hwpark.ch02dataaccess.controller.hypermedia;

import lombok.RequiredArgsConstructor;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.ItemRepository;

import reactor.core.publisher.Mono;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@RequestMapping("hypermedia")
public class HypermediaItemController {

    private final ItemRepository itemRepository;

    @GetMapping(path = "items")
    public Mono<CollectionModel<EntityModel<Item>>> findAll() {

        return itemRepository.findAll()
            .flatMap(item -> findOne(item.getId()))
            .collectList()
            .flatMap(entityModels -> linkTo(methodOn(HypermediaItemController.class)
                .findAll()).withSelfRel()
                .toMono()
                .map(selfLink -> CollectionModel.of(entityModels, selfLink)));
    }

    @GetMapping(path = "items/{id}")
    public Mono<EntityModel<Item>> findOne(@PathVariable String id) {
        // Spring Hateoas 의 정적 메서드인 WebFluxLinkBuilder.methodOn() 연산자를 사용해서 Controller 에 대한 Proxy 를 생성한다.
        HypermediaItemController controller = methodOn(HypermediaItemController.class);

        // WebFluxLinkBuilder.linkTo() 연산자를 사용해서 Controller 의 findOne() 에 대한 링크를 생성한다.
        // 현재 method 가 findOne() 이므로 self 라는 이름의 링크를 추가하고 reactor Mono 에 담아 반환
        Mono<Link> selfLink = linkTo(controller.findOne(id))
            .withSelfRel()
            .toMono();

        // 모든 상품을 반환하는 findAll() 메서드를 찾아서 Aggregate root 에 대한 링크를 생성한다.
        // IANA 표준에 따라 링크 이름을 item 으로 명명한다.
        Mono<Link> aggregateLink = linkTo(controller.findAll())
            .withRel(IanaLinkRelations.ITEM)
            .toMono();

        // 여러개의 비동기 요청을 실행하고 각 결과를 하나로 합치기 위해 Mono.zip() 메서드 사용
        // 예제에서는 findById() 메서드 호출과 selfLink, aggregateLink 생성 요청 결과를 타입 안전성이 보장되는 Reactor Tuple 타입에 넣고 Mono 로 감싸서 반환한다.
        // 마지막으로, map()을 통해 Tuple 에 담겨 있던 여러 비동기 요청 결과를 꺼내서 EntityModel 을 만들고, Mono 로 감싸서 반환한다.
        return Mono.zip(itemRepository.findById(id), selfLink, aggregateLink)
            .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3())));

    }

}
