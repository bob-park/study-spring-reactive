package com.hwpark.reactivesecurity.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;

import com.hwpark.commons.domain.Cart;
import com.hwpark.commons.domain.Item;
import com.hwpark.reactivesecurity.service.InventoryService;

import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final InventoryService inventoryService;

    @GetMapping
    public Mono<Rendering> home(Authentication auth) {
        return Mono.just(Rendering.view("home") // view 반환
            .modelAttribute("items", inventoryService.getInventory()) // model 반환
            .modelAttribute("cart",
                inventoryService.getCart(cartName(auth))
                    .defaultIfEmpty(new Cart(cartName(auth))))
            .modelAttribute("auth", auth)
            .build()
        );
    }

    @PostMapping(path = "add/{id}")
    public Mono<String> addToCart(Authentication auth, @PathVariable String id) {
        return inventoryService.addToCart(cartName(auth), id)
            .thenReturn("redirect:/");
    }

    @PostMapping
    public Mono<String> createItem(@ModelAttribute Item newItem) {
        return inventoryService.addItem(newItem) //
            .thenReturn("redirect:/");
    }

    @DeleteMapping("/delete/{id}")
    public Mono<String> deleteItem(@PathVariable String id) {
        return this.inventoryService.removeItem(id) //
            .thenReturn("redirect:/");
    }

    @GetMapping("/search")
    public Mono<Rendering> search(
        Authentication auth,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String description,
        @RequestParam boolean useAnd) {
        return Mono.just(Rendering.view("home")
            .modelAttribute("items",
                inventoryService.searchByExample(name, description, useAnd))
            .modelAttribute("cart",
                inventoryService.getCart(cartName(auth))
                    .defaultIfEmpty(new Cart(cartName(auth))))
            .build());
    }

    private String cartName(Authentication auth) {
        return auth.getName() + "'s Cart";
    }

}
