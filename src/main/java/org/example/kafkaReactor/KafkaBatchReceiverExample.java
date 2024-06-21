package org.example.kafkaReactor;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KafkaBatchReceiverExample {
    private static final String BOOTSTRAP_SERVERS = "172.16.100.235:9092";
    private static final String TOPIC = "test-topic";
    private final KafkaReceiver<Object, Object> receiver;

    public KafkaBatchReceiverExample() {
        Map<String, Object> receiverProps = new HashMap<>();
        receiverProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        receiverProps.put(ConsumerConfig.GROUP_ID_CONFIG, "reactor-group");
        receiverProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        receiverProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        receiverProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ReceiverOptions<Object, Object> receiverOptions = ReceiverOptions.create(receiverProps)
                .subscription(Collections.singleton(TOPIC));

        this.receiver = KafkaReceiver.create(receiverOptions);
    }

    public Flux<ReceiverRecord<Object, Object>> receiveBatchMessages() {
        return receiver.receive()
                .buffer(3)  // 3개의 메시지를 배치로 수신
                .flatMap(Flux::fromIterable)
                .doOnNext(record -> System.out.printf("Received message: topic-partition=%s offset=%d key=%s value=%s%n",
                        record.partition(),
                        record.offset(),
                        record.key(),
                        record.value()));
    }
}
