package org.example;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

public class FluxTest {

    @Test
    void testFluxJust() { // Hello, Reactor, World를 발생
        Flux<String> flux = Flux.just("Hello", "Reactor", "World");
        StepVerifier.create(flux)
                .expectNext("Hello")
                .expectNext("Reactor")
                .expectNext("World")
                .verifyComplete();
    }

    @Test
    void testFluxFromArray() { // 1, 2, 3을 발생
        Flux<Integer> flux = Flux.fromArray(new Integer[]{1, 2, 3});
        StepVerifier.create(flux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .verifyComplete();
    }

    @Test
    void testFluxFromIterable() { // A, B, C를 발생
        Flux<String> flux = Flux.fromIterable(Arrays.asList("A", "B", "C"));
        StepVerifier.create(flux)
                .expectNext("A")
                .expectNext("B")
                .expectNext("C")
                .verifyComplete();
    }

    @Test
    void testFluxFromStream() { // 1, 2, 3을 발생
        Flux<Integer> flux = Flux.fromStream(Stream.of(1, 2, 3));
        StepVerifier.create(flux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .verifyComplete();
    }

    @Test
    void testFluxRange() { // 1부터 5개의 숫자를 발생
        Flux<Integer> flux = Flux.range(1, 5);
        StepVerifier.create(flux)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .verifyComplete();
    }

    @Test
    void testFluxInterval() { // 0.1초 간격으로 3개의 숫자를 발생
        Flux<Long> flux = Flux.interval(Duration.ofMillis(100)).take(3);
        StepVerifier.create(flux)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void testFluxMap() { // 1, 2, 3을 받아서 2, 4, 6으로 변환
        Flux<Integer> flux = Flux.range(1, 3).map(i -> i * 2);
        StepVerifier.create(flux)
                .expectNext(2)
                .expectNext(4)
                .expectNext(6)
                .verifyComplete();
    }

    @Test
    void testFluxFlatMap() { // 1, 2, 3을 받아서 2, 4, 6으로 변환
        Flux<Integer> flux = Flux.just(1, 2, 3)
                .flatMap(i -> Flux.just(i * 2));
        StepVerifier.create(flux)
                .expectNext(2)
                .expectNext(4)
                .expectNext(6)
                .verifyComplete();
    }

    @Test
    void testFluxConcatMap() { // A, B, C를 받아서 소문자, 대문자로 변환 후 합치기
        Flux<String> flux = Flux.just("A", "B", "C")
                .concatMap(s -> Flux.just(s.toLowerCase(), s.toUpperCase()));
        StepVerifier.create(flux)
                .expectNext("a")
                .expectNext("A")
                .expectNext("b")
                .expectNext("B")
                .expectNext("c")
                .expectNext("C")
                .verifyComplete();
    }

    @Test
    void testFluxFilter() { // 짝수만 필터링
        Flux<Integer> flux = Flux.range(1, 5).filter(i -> i % 2 == 0);
        StepVerifier.create(flux)
                .expectNext(2)
                .expectNext(4)
                .verifyComplete();
    }

    @Test
    void testFluxMerge() { // flux1, flux2를 병합
        Flux<Integer> flux1 = Flux.range(1, 3);
        Flux<Integer> flux2 = Flux.range(4, 3);
        Flux<Integer> mergedFlux = Flux.merge(flux1, flux2);
        StepVerifier.create(mergedFlux)
                .expectNext(1, 2, 3, 4, 5, 6)
                .verifyComplete();
    }

    @Test
    void testFluxConcat() { // flux1, flux2를 연결
        Flux<Integer> flux1 = Flux.range(1, 3);
        Flux<Integer> flux2 = Flux.range(4, 3);
        Flux<Integer> concatenatedFlux = Flux.concat(flux1, flux2);
        StepVerifier.create(concatenatedFlux)
                .expectNext(1, 2, 3, 4, 5, 6)
                .verifyComplete();
    }

    @Test
    void testFluxZip() { // flux1, flux2를 조합
        Flux<Integer> flux1 = Flux.range(1, 3);
        Flux<Integer> flux2 = Flux.range(4, 3);
        Flux<String> zippedFlux = Flux.zip(flux1, flux2, (i1, i2) -> "Combined: " + (i1 + i2));
        StepVerifier.create(zippedFlux)
                .expectNext("Combined: 5")
                .expectNext("Combined: 7")
                .expectNext("Combined: 9")
                .verifyComplete();
    }

    @Test
    void testFluxOnErrorReturn() { // 3에서 예외 발생 시 -1로 대체
        Flux<Integer> flux = Flux.range(1, 5)
                .map(i -> {
                    if (i == 3) {
                        throw new RuntimeException("Exception at 3");
                    }
                    return i;
                })
                .onErrorReturn(-1);
        StepVerifier.create(flux)
                .expectNext(1, 2, -1)
                .verifyComplete();
    }

    @Test
    void testFluxOnErrorResume() { // 3에서 예외 발생 시 0, 1, 2로 대체
        Flux<Integer> flux = Flux.range(1, 5)
                .map(i -> {
                    if (i == 3) {
                        throw new RuntimeException("Exception at 3");
                    }
                    return i;
                })
                .onErrorResume(e -> Flux.just(0, 1, 2));
        StepVerifier.create(flux)
                .expectNext(1, 2, 0, 1, 2)
                .verifyComplete();
    }

    @Test
    void testFluxOnErrorMap() { // 3에서 예외 발생 시 IllegalArgumentException으로 변환
        Flux<Integer> flux = Flux.range(1, 5)
                .map(i -> {
                    if (i == 3) {
                        throw new RuntimeException("Exception at 3");
                    }
                    return i;
                })
                .onErrorMap(e -> new IllegalArgumentException("Mapped exception"));
        StepVerifier.create(flux)
                .expectNext(1, 2)
                .expectError(IllegalArgumentException.class)
                .verify();
    }
}