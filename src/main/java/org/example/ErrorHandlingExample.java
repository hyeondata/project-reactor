package org.example;

import reactor.core.publisher.Mono;

public class ErrorHandlingExample {
    public static void main(String[] args) {
        // 1. Mono 생성 및 에러 처리
        Mono<String> mono = Mono.just("Reactive Programming")
                .map(s -> {
                    if (s.contains("Reactive")) {
                        throw new RuntimeException("Error occurred!");
                    }
                    return s.toUpperCase();
                })
                .onErrorReturn("Fallback Value"); // 에러 발생 시 반환할 값

        // 2. Mono 구독
        mono.subscribe(System.out::println, System.err::println);
    }
}