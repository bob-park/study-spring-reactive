package com.hwpark.ch02dataaccess.controller.affordances;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.alps.Alps;
import org.springframework.hateoas.mediatype.alps.Type;
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

import reactor.core.publisher.Mono;

import static org.springframework.hateoas.mediatype.alps.Alps.alps;
import static org.springframework.hateoas.mediatype.alps.Alps.descriptor;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

@RequiredArgsConstructor
@RestController
@RequestMapping("affordances")
public class AffordancesItemController {

    private final ItemRepository itemRepository;

    @GetMapping(path = "")
    Mono<RepresentationModel<?>> root() {
        AffordancesItemController controller = methodOn(AffordancesItemController.class);

        Mono<Link> selfLink = linkTo(controller.root())
            .withSelfRel()
            .toMono();

        Mono<Link> itemsAggregateLink = linkTo(controller.findAll())
            .withRel(IanaLinkRelations.ITEM)
            .toMono();

        return selfLink.zipWith(itemsAggregateLink)
            .map(links -> Links.of(links.getT1(), links.getT2()))
            .map(links -> new RepresentationModel<>(links.toList()));
    }

    @GetMapping(path = "items")
    Mono<CollectionModel<EntityModel<Item>>> findAll() {
        AffordancesItemController controller = methodOn(AffordancesItemController.class);

        Mono<Link> aggregateRoot = linkTo(controller.findAll())
            .withSelfRel()
            .andAffordance(controller.addNewItem(null))
            .toMono();

        return itemRepository.findAll()
            .flatMap(item -> findOne(item.getId()))
            .collectList()
            .flatMap(models -> aggregateRoot
                .map(selfLink -> CollectionModel.of(models, selfLink)));
    }

    @GetMapping(path = "items/{id}") // <1>
    public Mono<EntityModel<Item>> findOne(@PathVariable String id) {
        AffordancesItemController controller = methodOn(AffordancesItemController.class); // <2>

        Mono<Link> selfLink = linkTo(controller.findOne(id)) //
            .withSelfRel() //
            .andAffordance(controller.updateItem(null, id)) // <3>
            .toMono();

        Mono<Link> aggregateLink = linkTo(controller.findAll()) //
            .withRel(IanaLinkRelations.ITEM) //
            .toMono();

        return Mono.zip(itemRepository.findById(id), selfLink, aggregateLink) //
            .map(o -> EntityModel.of(o.getT1(), Links.of(o.getT2(), o.getT3())));
    }

    @PostMapping(path = "items")
    Mono<ResponseEntity<?>> addNewItem(@RequestBody Mono<EntityModel<Item>> item) { // <2>
        return item
            .map(EntityModel::getContent)
            .flatMap(itemRepository::save)
            .map(Item::getId)
            .flatMap(this::findOne)
            .map(newModel -> ResponseEntity.created(newModel
                .getRequiredLink(IanaLinkRelations.SELF)
                .toUri()).body(newModel.getContent()));
    }

    @PutMapping(path = "items/{id}") // <1>
    public Mono<ResponseEntity<?>> updateItem(@RequestBody Mono<EntityModel<Item>> item, // <2>
        @PathVariable String id) {
        return item //
            .map(EntityModel::getContent) //
            .map(content -> new Item(id, content.getName(), // <3>
                content.getDescription(), content.getPrice())) //
            .flatMap(itemRepository::save) // <4>
            .then(findOne(id)) // <5>
            .map(model -> ResponseEntity.noContent() // <6>
                .location(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).build());
    }

    @GetMapping(path = "items/profile", produces = MediaTypes.ALPS_JSON_VALUE)
    public Alps profile() {
        return alps()
            .descriptor(Collections.singletonList(descriptor()
                .id(Item.class.getSimpleName() + "-representation")
                .descriptor(
                    Arrays.stream(Item.class.getDeclaredFields())
                        .map(field -> descriptor()
                            .name(field.getName())
                            .type(Type.SEMANTIC)
                            .build())
                        .collect(Collectors.toList()))
                .build()))
            .build();
    }

}
