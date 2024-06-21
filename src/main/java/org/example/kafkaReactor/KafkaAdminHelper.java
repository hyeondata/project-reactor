package org.example.kafkaReactor;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class KafkaAdminHelper {
    private static final String BOOTSTRAP_SERVERS = "172.16.100.235:9092";
    private static final String TOPIC = "test-topic";
    private final AdminClient adminClient;

    public KafkaAdminHelper() {
        Map<String, Object> adminProps = new HashMap<>();
        adminProps.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        this.adminClient = AdminClient.create(adminProps);
    }

    public void recreateTopic() {
        try {
            // Delete the topic
            adminClient.deleteTopics(Collections.singletonList(TOPIC)).all().get();

            // Wait for deletion to propagate
            Thread.sleep(1000);

            // Recreate the topic
            NewTopic newTopic = new NewTopic(TOPIC, 1, (short) 1); // 1 partition, 1 replication factor
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to recreate topic", e);
        }
    }

    public void close() {
        adminClient.close();
    }
}