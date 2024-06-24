package org.example;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BackPressure {
    @Test
    void testBackPressure() throws InterruptedException {
        Flux<Integer> numberGenerator = Flux.create(x -> {
            System.out.println("Requested Event: " + x.requestedFromDownstream());

            int number = 1;
            while(number < 100) {
                x.next(number);
                number++;
            }
            x.complete();
        });

        CountDownLatch latch = new CountDownLatch(1);
        numberGenerator.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(1);
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println(value);
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                throwable.printStackTrace();
                latch.countDown();
            }

            @Override
            protected void hookOnComplete() {
                latch.countDown();
            }
        });
    }

    @Test
    void tastBackPressureError() throws InterruptedException {
        Flux<Integer> numberGenerator = Flux.create(x -> {
            System.out.println("Requested Events: " + x.requestedFromDownstream());

            int number = 1;
            while(number < 100) {
                x.next(number);
                number++;
            }
            x.complete();
        }, FluxSink.OverflowStrategy.ERROR);

        CountDownLatch latch = new CountDownLatch(1);
        numberGenerator.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                request(1);
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println(value);
            }

            @Override
            protected void hookOnError(Throwable throwable) {
                throwable.printStackTrace();
                latch.countDown();
            }

            @Override
            protected void hookOnComplete() {
                latch.countDown();
            }
        });

        latch.await(1L, TimeUnit.SECONDS);
    }

    @Test
    void onBackPreesureDrop() throws InterruptedException {
        Flux<Integer> numberGenerator = Flux.create(x -> {
            System.out.println("Request Events: " + x.requestedFromDownstream());
            int number = 1;
            while(number < 100) {
                x.next(number);
                number++;
            }
            x.complete();
        });

        CountDownLatch latch = new CountDownLatch(1);
        numberGenerator
                .onBackpressureDrop(x -> System.out.println("Dropped : " + x))
                .subscribe(new BaseSubscriber<Integer>() {
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    protected void hookOnNext(Integer value) {
                        System.out.println(value);
                    }

                    @Override
                    protected void hookOnError(Throwable throwable) {
                        throwable.printStackTrace();
                        latch.countDown();
                    }

                    @Override
                    protected void hookOnComplete() {
                        latch.countDown();
                    }
                });

        latch.await(1L, TimeUnit.SECONDS);
    }

    @Test
    void onBackpressureLatestExample() throws InterruptedException {
        // 생산자가 10밀리초마다 데이터를 생성
        Flux<Long> producerGen = Flux.interval(Duration.ofMillis(10))
                .onBackpressureLatest() // 백프레셔가 발생하면 최신 데이터만 유지
                .doOnNext(item -> System.out.println(LocalTime.now() + " - Produced: " + item))
                .doOnComplete(() -> System.out.println("Producer completed"));

        producerGen
                .publishOn(Schedulers.parallel())
                .doOnNext(item -> {
                    try {
                        // 소비자가 50밀리초마다 데이터를 처리하도록 지연
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(LocalTime.now() + " - Consumed: " + item);
                })
                .doOnComplete(() -> System.out.println("Consumer completed"))
                .subscribe();

        // 메인 스레드를 유지하여 Flux가 동작하도록 함
        Thread.sleep(5000);
    }

    @Test
    void onBackpressureBufferExample() {
        Flux<Integer> producer = Flux.range(1, 100);

        producer
                .onBackpressureBuffer(10)
                .subscribe(value -> {
                    try {
                        Thread.sleep(100); // Simulating a slow consumer
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Received: " + value);
                });
    }

    @Test
    void onBackPressureErrorExample() throws InterruptedException     {
            Flux
                    .interval(Duration.ofMillis(1L))
                    .onBackpressureError() //error 전략 사용
                    .doOnNext(data -> System.out.println("# doOnNext: " + data))
                    .publishOn(Schedulers.parallel())
                    .subscribe(data -> {
                                try {
                                    Thread.sleep(5L);
                                } catch (InterruptedException e) {}
                                System.out.println("# onNext: " + data);
                            },
                            error -> System.out.println("# Error " + error));

            Thread.sleep(2000L);
        }
    }

}
