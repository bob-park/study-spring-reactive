package com.hwpark.ch02dataaccess.service;

import java.util.Collections;

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

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class InventoryServiceTest {

    // CUT : 테스트 대상 Class
    /*
     아무런, Annotation 도 붙지 않으며, 테스트할때 초기화된다.
     */
    InventoryService inventoryService;

    @MockBean // 가짜 Bean 생성 및 Spring Container 에 등록
    private ItemRepository itemRepository;

    @MockBean
    private CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        // @MockBean 다음과 같이 동작한다.
//        itemRepository = mock(ItemRepository.class);
//        cartRepository = mock(CartRepository.class);

        // 테스트 데이터 정의
        Item sampleItem = new Item("item1", "TV tray", "Alf TV tray", 19.99);
        CartItem sampleCartItem = new CartItem(sampleItem);
        Cart sampleCart = new Cart("My Cart", Collections.singletonList(sampleCartItem));

        // 협력자와의 상호작용 정의
        when(cartRepository.findById(anyString())).thenReturn(Mono.empty());
        when(itemRepository.findById(anyString())).thenReturn(Mono.just(sampleItem));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(sampleCart));

        inventoryService = new InventoryService(itemRepository, cartRepository); // MockBean 주입

    }

    // Top-Level 방식
    @Test
    void addItemToEmptyCartShouldProduceOnCartItem() {

        inventoryService.addToCart("My Cart", "item1")
            .as(StepVerifier::create)// 메서드 레퍼런스로 연결해서, 테스트 기능을 전담하는 reactor type handler 생성
            .expectNextMatches(cart -> { // 결과 검증
                assertThat(cart.getCartItems())
                    .extracting(CartItem::getQuantity)
                    .containsExactlyInAnyOrder(1);

                assertThat(cart.getCartItems())
                    .extracting(CartItem::getItem)
                    .containsExactly(new Item("item1", "TV tray", "Alf TV tray", 19.99));

                return true;
            })
            .verifyComplete();

    }

    // 다른 방식
    // ! 이 방식을 사용하는 것 보다 Top-Level 방식이 테스트 코드의 의도가 더 분명히 드러나기 때문에, Top-Level 방식을 선호한다.
    @Test
    void alternativeWayToTest() {
        StepVerifier.create(inventoryService.addToCart("My Cart", "item1"))
            .expectNextMatches(cart -> { // 결과 검증
                assertThat(cart.getCartItems())
                    .extracting(CartItem::getQuantity)
                    .containsExactlyInAnyOrder(1);

                assertThat(cart.getCartItems())
                    .extracting(CartItem::getItem)
                    .containsExactly(new Item("item1", "TV tray", "Alf TV tray", 19.99));

                return true;
            })
            .verifyComplete();
    }

}