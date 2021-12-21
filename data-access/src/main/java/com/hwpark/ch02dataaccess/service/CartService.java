package com.hwpark.ch02dataaccess.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.hwpark.ch02dataaccess.domain.Cart;
import com.hwpark.ch02dataaccess.domain.CartItem;
import com.hwpark.ch02dataaccess.repository.CartRepository;
import com.hwpark.ch02dataaccess.repository.ItemRepository;

import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CartService {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    public Mono<Cart> addToCart(String cartId, String id) {
        return cartRepository.findById("My Cart")
            .defaultIfEmpty(new Cart("My Cart"))
            .flatMap(cart -> cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getItem().getId().equals(id)).findAny()
                .map(cartItem -> {
                    cartItem.increment();
                    return Mono.just(cart);
                })
                .orElseGet(() -> itemRepository.findById(id)
                    .map(CartItem::new)
                    .map(cartItem -> {
                        cart.getCartItems().add(cartItem);
                        return cart;
                    }))
            )
            .flatMap(cartRepository::save);
    }

}
