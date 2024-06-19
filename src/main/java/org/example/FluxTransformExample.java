package org.example;

import reactor.core.publisher.Flux;

public class FluxTransformExample {
    public static void main(String[] args) {
        // 1. Flux 생성 및 변환, 필터링
        Flux<String> flux = Flux.just("Spring", "Spring Boot", "Spring Cloud", "Project Reactor")
                .map(String::toUpperCase) // 모든 문자열을 대문자로 변환
                .filter(s -> s.startsWith("SPRING")); // "SPRING"으로 시작하는 문자열만 필터링

        // 2. Flux 구독
        flux.subscribe(System.out::println);
    }
}
