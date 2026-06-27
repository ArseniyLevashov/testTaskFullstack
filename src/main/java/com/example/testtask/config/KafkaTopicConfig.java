package com.example.testtask.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic apiDataTopic() {
        return TopicBuilder.name("api-data")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic apiErrorsTopic() {
        return TopicBuilder.name("api-errors")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
