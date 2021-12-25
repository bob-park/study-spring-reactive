package com.hwpark.ch02dataaccess.blockhound;

import java.time.Duration;
import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.hwpark.ch02dataaccess.domain.Cart;
import com.hwpark.ch02dataaccess.domain.CartItem;
import com.hwpark.ch02dataaccess.domain.Item;
import com.hwpark.ch02dataaccess.repository.CartRepository;
import com.hwpark.ch02dataaccess.repository.ItemRepository;
import com.hwpark.ch02dataaccess.service.AltInventoryService;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class BloodhoundIntegrationTest {

    AltInventoryService inventoryService;

    @MockBean
    ItemRepository itemRepository;

    @MockBean
    CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);
        CartItem sampleCartItem = new CartItem(sampleItem);
        Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

        when(cartRepository.findById(anyString())).thenReturn(Mono.<Cart>empty().hide());
        when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

        inventoryService = new AltInventoryService(itemRepository, cartRepository);

    }

    @Test
    void blockHoundShouldTrapBlockingCall() {
        Mono.delay(Duration.ofSeconds(1))
            .flatMap(tick -> inventoryService.addToCart("My Cart", "item1"))
            .as(StepVerifier::create)
            .verifyErrorSatisfies(throwable ->
                assertThat(throwable)
                    .hasMessageContaining("block()/blockFirst()/blockLast() are blocking"));
    }

}
