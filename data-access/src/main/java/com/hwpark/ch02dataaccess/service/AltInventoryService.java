package com.hwpark.ch02dataaccess.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.hwpark.ch02dataaccess.domain.Cart;
import com.hwpark.ch02dataaccess.domain.CartItem;
import com.hwpark.ch02dataaccess.repository.CartRepository;
import com.hwpark.ch02dataaccess.repository.ItemRepository;

import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class AltInventoryService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    public Mono<Cart> addToCart(String cartId, String itemId) {

        Cart myCart = cartRepository.findById(cartId)
            .defaultIfEmpty(new Cart(cartId))
            .block();

        return myCart.getCartItems().stream()
            .filter(cartItem -> cartItem.getItem().getId().equals(itemId))
            .findAny()
            .map(cartItem -> {
                cartItem.increment();
                return Mono.just(myCart);
            })
            .orElseGet(() -> itemRepository.findById(itemId)
                .map(CartItem::new)
                .map(cartItem -> {
                    myCart.getCartItems().add(cartItem);
                    return myCart;
                })
            ).flatMap(cartRepository::save);

    }
}
