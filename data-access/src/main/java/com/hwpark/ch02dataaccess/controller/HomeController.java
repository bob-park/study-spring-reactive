package com.hwpark.ch02dataaccess.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;

import com.hwpark.ch02dataaccess.domain.Cart;
import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.CartRepository;
import com.hwpark.ch02dataaccess.repository.ItemRepository;
import com.hwpark.ch02dataaccess.service.CartService;
import com.hwpark.ch02dataaccess.service.InventoryService;

import reactor.core.publisher.Mono;

/**
 * Spring WebFlux 에서 주의할점
 *
 * <pre>
 *      ! @DeleteMapping 이 붙은 Controller Method 로 요청을 전달하는 특수 필터 포함되어 있다.
 *          - 이 필터는 기본적으로 활성화 되어 있지 않으며, 다음 설정을 추가해야 활성화된다.
 *              - spring.webflux.hiddenmethod.filter.enable=true
 * </pre>
 */
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

    private final CartService cartService;
    private final InventoryService inventoryService;

    @GetMapping
    public Mono<Rendering> home() {
        // Model 를 반환하기 위해 WebFlux Container 인 Rendering 반환
        // ! 단, Reactive Stream 을 Template Engine 이 지원해야 한다.
        return Mono.just(Rendering.view("home") // view 반환
            .modelAttribute("items", itemRepository.findAll()) // model 반환
            .modelAttribute("cart",
                cartRepository.findById("My Cart")
                    .defaultIfEmpty(new Cart("My Cart"))) // 값이 없는 경우 default 값 반환
            .build()
        );
    }

    @PostMapping(path = "add/{id}")
    public Mono<String> addToCart(@PathVariable String id) {
        return inventoryService.addToCart("My Cart", id)
            .thenReturn("redirect:/");
    }

    @PostMapping
    public Mono<String> createItem(@ModelAttribute Item newItem) {
        return this.itemRepository.save(newItem) //
            .thenReturn("redirect:/");
    }

    @DeleteMapping("/delete/{id}")
    public Mono<String> deleteItem(@PathVariable String id) {
        return this.itemRepository.deleteById(id) //
            .thenReturn("redirect:/");
    }

    @GetMapping("/search")
    public Mono<Rendering> search(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String description,
        @RequestParam boolean useAnd) {
        return Mono.just(Rendering.view("home")
            .modelAttribute("items",
                inventoryService.searchByExample(name, description, useAnd))
            .modelAttribute("cart",
                this.cartRepository.findById("My Cart")
                    .defaultIfEmpty(new Cart("My Cart")))
            .build());
    }

}
