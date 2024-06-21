package org.example.kafkaReactor;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReactorKafkaExample {

    private static final String BOOTSTRAP_SERVERS = "172.16.100.235:9092";
    private static final String TOPIC = "test-topic";

    public static void main(String[] args) {
        ReactorKafkaExample example = new ReactorKafkaExample();
        example.sendMessages();
        example.receiveMessages();
    }

    public void sendMessages() {
        Map<String, Object> senderProps = new HashMap<>();
        senderProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        senderProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        senderProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        SenderOptions<String, String> senderOptions = SenderOptions.create(senderProps);

        KafkaSender<String, String> sender = KafkaSender.create(senderOptions);
        Flux<SenderRecord<String, String, Integer>> outboundFlux = Flux.range(1, 10)
                .map(i -> SenderRecord.create(TOPIC, null, null, "key-" + i, "value-" + i, i));

        sender.send(outboundFlux)
                .doOnError(e -> System.err.println("Send failed: " + e))
                .doOnNext(r -> System.out.printf("Message %d sent successfully, topic-partition=%s-%d offset=%d%n",
                        r.correlationMetadata(), r.recordMetadata().topic(), r.recordMetadata().partition(), r.recordMetadata().offset()))
                .subscribe();
    }

    public void receiveMessages() {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "reactor-group");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        ReceiverOptions<Object, Object> receiverOptions = ReceiverOptions.create(consumerProps)
                .subscription(Collections.singleton(TOPIC));

        KafkaReceiver<Object, Object> receiver = KafkaReceiver.create(receiverOptions);
        receiver.receive()
                .doOnError(e -> System.err.println("Receive failed: " + e))
                .doOnNext(record -> System.out.printf("Received message: key=%s, value=%s, topic-partition=%s-%d offset=%d%n",
                        record.key(), record.value(), record.topic(), record.partition(), record.offset()))
                .subscribe();
    }
}