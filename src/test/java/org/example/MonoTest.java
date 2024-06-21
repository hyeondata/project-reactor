package org.example;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.Callable;

public class MonoTest {

    @Test
    void testMonoJust() { // Hello, Reactor!를 발생
        Mono<String> mono = Mono.just("Hello, Reactor!");
        StepVerifier.create(mono)
                .expectNext("Hello, Reactor!")
                .verifyComplete();
    }

    @Test
    void testMonoEmpty() { // 아무 값도 발생하지 않음
        Mono<String> mono = Mono.empty();
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    void testMonoError() { // 에러 발생
        Mono<String> mono = Mono.error(new RuntimeException("Exception occurred"));
        StepVerifier.create(mono)
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testMonoFromCallable() { // Callable로부터 결과를 발생
        Callable<String> callable = () -> "Callable result";
        Mono<String> mono = Mono.fromCallable(callable);
        StepVerifier.create(mono)
                .expectNext("Callable result")
                .verifyComplete();
    }

    @Test
    void testMonoFromSupplier() { // Supplier로부터 결과를 발생
        Mono<String> mono = Mono.fromSupplier(() -> "Supplier result");
        StepVerifier.create(mono)
                .expectNext("Supplier result")
                .verifyComplete();
    }

    @Test
    void testMonoFromRunnable() { // Runnable 실행
        Mono<Void> mono = Mono.fromRunnable(() -> System.out.println("Runnable executed"));
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    void testMonoDelay() { // 1초 후에 0을 발생
        Mono<Long> mono = Mono.delay(Duration.ofSeconds(1));
        StepVerifier.create(mono)
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void testMonoMap() { // 1을 받아 2를 발생
        Mono<Integer> mono = Mono.just(1).map(i -> i * 2);
        StepVerifier.create(mono)
                .expectNext(2)
                .verifyComplete();
    }

    @Test
    void testMonoFlatMap() { // 1을 받아 2를 발생
        Mono<Integer> mono = Mono.just(1).flatMap(i -> Mono.just(i * 2));
        StepVerifier.create(mono)
                .expectNext(2)
                .verifyComplete();
    }

    @Test
    void testMonoFlatMapMany() { // 1을 받아 1, 2를 발생
        Mono<Integer> mono = Mono.just(1);
        StepVerifier.create(mono.flatMapMany(i -> Flux.just(i, i + 1)))
                .expectNext(1, 2)
                .verifyComplete();
    }

    @Test
    void testMonoThen() { // 1을 발생 후 완료
        Mono<Void> mono = Mono.just(1).then();
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    void testMonoThenReturn() { // 1을 발생 후 "Completed"를 발생
        Mono<String> mono = Mono.just(1).thenReturn("Completed");
        StepVerifier.create(mono)
                .expectNext("Completed")
                .verifyComplete();
    }

    @Test
    void testMonoZip() { // 1, 2를 받아 "Combined: 3"을 발생
        Mono<Integer> mono1 = Mono.just(1);
        Mono<Integer> mono2 = Mono.just(2);
        Mono<String> mono = Mono.zip(mono1, mono2, (i1, i2) -> "Combined: " + (i1 + i2));
        StepVerifier.create(mono)
                .expectNext("Combined: 3")
                .verifyComplete();
    }

    @Test
    void testMonoWhen() { // 1, 2를 받아 완료
        Mono<Integer> mono1 = Mono.just(1);
        Mono<Integer> mono2 = Mono.just(2);
        Mono<Void> mono = Mono.when(mono1, mono2);
        StepVerifier.create(mono)
                .verifyComplete();
    }

    @Test
    void testMonoOnErrorReturn() { // 에러 발생 시 -1로 대체
        Mono<Integer> mono = Mono.<Integer>error(new RuntimeException("Exception occurred"))
                .onErrorReturn(-1);
        StepVerifier.create(mono)
                .expectNext(-1)
                .verifyComplete();
    }

    @Test
    void testMonoOnErrorResume() { // 에러 발생 시 0으로 대체
        Mono<Integer> mono = Mono.<Integer>error(new RuntimeException("Exception occurred"))
                .onErrorResume(e -> Mono.just(0));
        StepVerifier.create(mono)
                .expectNext(0)
                .verifyComplete();
    }

    @Test
    void testMonoOnErrorMap() { // 에러 발생 시 IllegalArgumentException으로 변환
        Mono<Integer> mono = Mono.<Integer>error(new RuntimeException("Exception occurred"))
                .onErrorMap(e -> new IllegalArgumentException("Mapped exception"));
        StepVerifier.create(mono)
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}