package com.hwpark.ch02dataaccess.blockhound;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BlockingTest {

    // 해당 테스트를 진행할 경우 Blocking call 메세지와 함께 테스트에 실패한다.
    @Test
    void threadSleepIsABlocking() {

        Mono.delay(Duration.ofSeconds(1))
            .flatMap(tick -> {
                try {
                    Thread.sleep(10);
                    return Mono.just(true);
                } catch (InterruptedException e) {
                    return Mono.error(e);
                }
            })
            .as(StepVerifier::create)
            .verifyComplete();
    }

}
