package org.example.kafkaReactor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.util.HashMap;
import java.util.Map;

public class KafkaBatchSenderExample {

    private static final String BOOTSTRAP_SERVERS = "172.16.100.235:9092";
    private static final String TOPIC = "test-topic";
    private final KafkaSender<String, String> sender;

    public KafkaBatchSenderExample() {
        Map<String, Object> senderProps = new HashMap<>();
        senderProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        senderProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        senderProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        SenderOptions<String, String> senderOptions = SenderOptions.create(senderProps);
        this.sender = KafkaSender.create(senderOptions);
    }

    public Flux<SenderResult<Object>> sendBatchMessages() {
        return sender.send(Flux.just(
                        SenderRecord.create(new ProducerRecord<>(TOPIC, "key1", "Batch message 1"), null),
                        SenderRecord.create(new ProducerRecord<>(TOPIC, "key2", "Batch message 2"), null),
                        SenderRecord.create(new ProducerRecord<>(TOPIC, "key3", "Batch message 3"), null)
                ))
                .doOnError(e -> System.out.println("Send failed, exception: " + e))
                .doOnNext(r -> System.out.println("Message sent: " + r.recordMetadata()));
    }
}
