package com.hwpark.ch02dataaccess.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;

import com.hwpark.ch02dataaccess.domain.Item;

import reactor.core.publisher.Flux;

/**
 * ! 주의
 *
 * <pre>
 *      - Spring Data Commons 의 있는 Repository 를 구현한 것
 *      - 하지만, 반환타입은 모두 Reactor 타입
 *      - 따라서, Reactor 타입을 subscribe() 하기전에는 아무일도 일어나지 않는다.
 *      - Reactive Repository 는 Netty 가 시작되면 Subscriber 가 Application 시작 Thread 로 하여금 이벤트 루프를 DeadLock 상태에 빠트릴 수 있는 위험이 분명히 존재한다.
 *      - 따라서, Application 시작시 테스트 데이터 같이 데이터를 로딩이 필요한 경우 Blocking Code 로 해야한다.
 * </pre>
 * <p>
 * <p>
 * ReactiveQueryByExampleExecutor
 *
 * <pre>
 *       - Spring Data Commons 로 인해 Method 이름에 Query 를 추출할 수 있지만, 복잡한 조건은 Method 이름의 길이가 길어질 뿐 아니라, 복잡한 조건은 해결할 수없다.
 *       - 따라서, ReactiveQueryByExampleExecutor 를 통해 조건을 다양하게 줄 수 있다.
 * </pre>
 */
public interface ItemRepository extends ReactiveMongoRepository<Item, String>,
    ReactiveQueryByExampleExecutor<Item> {

}
