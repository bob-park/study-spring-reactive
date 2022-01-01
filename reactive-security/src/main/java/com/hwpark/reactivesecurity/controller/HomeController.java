package com.hwpark.reactivesecurity.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public Mono<Rendering> home(
        @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
        @AuthenticationPrincipal OAuth2User oAuth2User
    ) {
        return Mono.just(Rendering.view("home") // view 반환
            .modelAttribute("items", inventoryService.getInventory()) // model 반환
            .modelAttribute("cart",
                inventoryService.getCart(cartName(oAuth2User))
                    .defaultIfEmpty(new Cart(cartName(oAuth2User))))
            .modelAttribute("userName", oAuth2User.getName())
            .modelAttribute("authorities", oAuth2User.getAuthorities())
            .modelAttribute("clientName", authorizedClient.getClientRegistration().getClientName())
            .modelAttribute("userAttributes", oAuth2User.getAttributes())
            .build()
        );
    }

    @PostMapping(path = "add/{id}")
    public Mono<String> addToCart(
//        Authentication auth,
        @AuthenticationPrincipal OAuth2User oAuth2User,
        @PathVariable String id) {
        return inventoryService.addToCart(cartName(oAuth2User), id)
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

//    @GetMapping("/search")
//    public Mono<Rendering> search(
//        Authentication auth,
//        @RequestParam(required = false) String name,
//        @RequestParam(required = false) String description,
//        @RequestParam boolean useAnd) {
//        return Mono.just(Rendering.view("home")
//            .modelAttribute("items",
//                inventoryService.searchByExample(name, description, useAnd))
//            .modelAttribute("cart",
//                inventoryService.getCart(cartName(auth))
//                    .defaultIfEmpty(new Cart(cartName(auth))))
//            .build());
//    }

    private String cartName(OAuth2User auth) {
        return auth.getName() + "'s Cart";
    }

}
