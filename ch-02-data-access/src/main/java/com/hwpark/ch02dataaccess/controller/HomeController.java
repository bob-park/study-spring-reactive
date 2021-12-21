package com.hwpark.ch02dataaccess.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;

import com.hwpark.ch02dataaccess.domain.Cart;
import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.CartRepository;
import com.hwpark.ch02dataaccess.repository.ItemRepository;

import reactor.core.publisher.Mono;

/**
 * Spring WebFlux 에서 주의할점
 *
 * <pre>
 *      ! @DeleteMapping 이 붙은 Controller Method 로 요청을 전달하는 특수 필터 포함되어 있다.
 *          - 이 필터는 기본적으로 활성화 되어 있지 않으며, 다음 설정을 추가해야 활성화된다.
 *              - spring.webflux.hiddenmethod.filter.enable=true
 * </pre>
 *
 */
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;

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

}