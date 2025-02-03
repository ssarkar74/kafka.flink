package com.sarkar.kafka.stream.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;

@Slf4j
@Configuration
@EnableKafkaStreams
public class StreamConfig {
    @Bean
    public NewTopic topicBuilder() {
        return TopicBuilder.name("orders")
                .partitions(2)
                .replicas(1)
                .build();
    }
}
