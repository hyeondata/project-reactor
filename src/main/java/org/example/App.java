package org.example;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        List<String> words = Arrays.asList("Hello", "Reactor", "World"); // 단어 목록

        Flux<String> flux = Flux.fromIterable(words) // Flux 생성
                .map(String::toUpperCase) // 대문자로 변환
                .doOnNext(word -> System.out.println("Processing word: " + word)) // 각 요소가 처리될 때 실행되는 사이드 이펙트 연산입니다.
                .doOnComplete(() -> System.out.println("All words processed")); // 모든 요소의 처리가 완료되었을 때 실행됩니다.


        /**
         * 첫 번째 인자는 각 요소가 처리될 때 호출됩니다.
         * 두 번째 인자는 에러가 발생했을 때 호출됩니다.
         * 세 번째 인자는 모든 처리가 완료되었을 때 호출됩니다.
         */
        flux.subscribe( // Flux를 구독합니다.
                word -> System.out.println("Subscriber received: " + word),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Subscription completed")
        );

        /**
         *
         */

        // Mono 예제: 단일 요소를 비동기적으로 처리
        Mono<String> mono = Mono.just("Hello, Mono!")
                .map(String::toUpperCase)
                .doOnNext(word -> System.out.println("Processing single word: " + word)) // 각 요소가 처리될 때 실행되는 사이드 이펙트 연산입니다.
                .doOnTerminate(() -> System.out.println("Mono processing terminated")); // 처리가 완료되었을 때 실행됩니다.

        mono.subscribe(
                word -> System.out.println("Subscriber received: " + word),
                error -> System.err.println("Error: " + error),
                () -> System.out.println("Mono subscription completed")
        );
        System.out.println( "Hello World!" );
    }
}
