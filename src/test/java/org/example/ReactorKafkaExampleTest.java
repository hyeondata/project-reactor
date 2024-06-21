package org.example;

import org.example.kafkaReactor.KafkaAdminHelper;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
public class ReactorKafkaExampleTest {

    private static final String BOOTSTRAP_SERVERS = "172.16.100.235:9092";
    private static final String TOPIC = "test-topic";

    @Test
    public void testKafkaSendAndReceive() {
        KafkaAdminHelper kafkaAdminHelper = new KafkaAdminHelper();
        kafkaAdminHelper.recreateTopic();  // 토픽 비우기
        // Producer 설정
        Map<String, Object> senderProps = new HashMap<>();
        senderProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        senderProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        senderProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        SenderOptions<String, String> senderOptions = SenderOptions.create(senderProps);

        KafkaSender<String, String> sender = KafkaSender.create(senderOptions);
        Flux<SenderRecord<String, String, Integer>> outboundFlux = Flux.range(1, 10)
                .map(i -> SenderRecord.create(TOPIC, null, null, "key-" + i, "value-" + i, i));

        Flux<SenderResult<Integer>> resultFlux = sender.send(outboundFlux);

        // StepVerifier를 사용하여 전송 결과 검증
        StepVerifier.create(resultFlux)
                .expectNextCount(10)
                .verifyComplete();

        // 잠시 대기하여 메시지가 전송될 시간을 확보합니다.
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Consumer 설정
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put("bootstrap.servers", BOOTSTRAP_SERVERS);
        consumerProps.put("group.id", "reactor-group");
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("auto.offset.reset", "earliest");
        ReceiverOptions<Object, Object> receiverOptions = ReceiverOptions.create(consumerProps)
                .subscription(Collections.singleton(TOPIC));

        KafkaReceiver<Object, Object> receiver = KafkaReceiver.create(receiverOptions);

        // StepVerifier를 사용하여 수신 결과 검증
        StepVerifier.create(receiver.receive().take(10))
                .expectNextCount(9)  // 0~9 개 까지 차례로 들어간다
                .consumeNextWith(record -> {
                    System.out.println("Received message: key=" + record.key() + ", value=" + record.value());
                    assertNotNull(record.key());
                    assertNotNull(record.value());
                })
                .verifyComplete();

    }
}

