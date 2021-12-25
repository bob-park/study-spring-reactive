package com.hwpark.ch02dataaccess.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;

import com.hwpark.ch02dataaccess.domain.Cart;
import com.hwpark.ch02dataaccess.domain.CartItem;
import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.CartRepository;
import com.hwpark.ch02dataaccess.repository.ItemRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class InventoryService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

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

    public Mono<Cart> addToCart(String cartId, String id) {
        return cartRepository.findById(cartId)
            .log("foundCart")
            .defaultIfEmpty(new Cart(cartId))
            .log("emptyCart")
            .flatMap(cart -> cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getItem().getId().equals(id)).findAny()
                .map(cartItem -> {
                    cartItem.increment();
                    return Mono.just(cart).log("newCartItem");
                })
                .orElseGet(() -> itemRepository.findById(id)
                    .log("fetchedItem")
                    .map(CartItem::new)
                    .log("cartItem")
                    .map(cartItem -> {
                        cart.getCartItems().add(cartItem);
                        return cart;
                    }))
                .log("addedCartItem")
            )
            .log("cartWithAnotherItem")
            .flatMap(cartRepository::save)
            .log("savedCart");
    }

    public Mono<Void> removeItem(String id) {
        return itemRepository.deleteById(id);
    }

    public Mono<Item> addItem(Item newItem) {
        return itemRepository.save(newItem);
    }

    public Flux<Item> getInventory() {
        return itemRepository.findAll();
    }

    public Mono<Cart> getCart(String cartId) {
        return cartRepository.findById(cartId);
    }

}
