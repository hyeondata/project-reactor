package org.example;

import org.example.kafkaReactor.KafkaAdminHelper;
import org.example.kafkaReactor.KafkaBatchReceiverExample;
import org.example.kafkaReactor.KafkaBatchSenderExample;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;
import reactor.test.StepVerifier;
public class ReactorKafkaBatchTest {

    @Test
    public void testKafkaBatchSendAndReceive() {
        KafkaAdminHelper kafkaAdminHelper = new KafkaAdminHelper();
        kafkaAdminHelper.recreateTopic();  // 토픽 비우기
        KafkaBatchSenderExample kafkaBatchSender = new KafkaBatchSenderExample();
        KafkaBatchReceiverExample kafkaBatchReceiver = new KafkaBatchReceiverExample();

        // 배치 메시지 보내기
        Flux<SenderResult<Object>> sendFlux = kafkaBatchSender.sendBatchMessages();

        StepVerifier.create(sendFlux)
                .expectNextCount(3)
                .expectComplete()
                .verify();



        // 배치 메시지 받기
        Flux<ReceiverRecord<Object, Object>> receiveFlux = kafkaBatchReceiver.receiveBatchMessages()
                .take(3); // 3개의 메시지만 받도록 제한

        StepVerifier.create(receiveFlux)
                .expectNextMatches(record -> "Batch message 1".equals(record.value()))
                .expectNextMatches(record -> "Batch message 2".equals(record.value()))
                .expectNextMatches(record -> "Batch message 3".equals(record.value()))
                .expectComplete()
                .verify();
    }
}
