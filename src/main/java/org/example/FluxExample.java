package org.example;

import reactor.core.publisher.Flux;

public class FluxExample {
    public static void main(String[] args) {
        // 1. Flux 생성
        Flux<String> flux = Flux.just("Spring", "Spring Boot", "Spring Cloud", "Project Reactor");

        // 2. Flux 구독
        flux.subscribe(System.out::println);
    }
}
