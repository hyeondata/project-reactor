package org.example;

import reactor.core.publisher.Mono;

public class AsyncChainExample {
    public static void main(String[] args) {
        // 1. Mono 생성 및 체인 호출
        Mono.just("Reactive Programming")
                .map(String::toUpperCase) // 대문자로 변환
                .flatMap(s -> appendExclamationMark(s)) // 비동기 함수 호출
                .subscribe(System.out::println); // 구독 및 출력
    }

    // 비동기 함수
    private static Mono<String> appendExclamationMark(String s) {
        return Mono.just(s + "!");
    }
}