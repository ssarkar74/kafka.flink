package com.sarkar.kafka.stream.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.DefaultProductionExceptionHandler;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.config.TopicBuilder;

import java.util.HashMap;
import java.util.Map;

import static com.sarkar.kafka.stream.Constant.*;

@Slf4j
@Configuration
public class StreamConfig {
    @Bean
    public NewTopic topicBuilder() {
        return TopicBuilder.name("orders")
                .partitions(2)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic clientopicBuilder() {
        return TopicBuilder.name(CLIENT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic productTopicBuilder() {
        return TopicBuilder.name(PRODUCT_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic orderTopicBuilder() {
        return TopicBuilder.name(ORDER_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic clientTriggerTopicBuilder() {
        return TopicBuilder.name(CLIENT_TRIGGER_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic productTriggerTopicBuilder() {
        return TopicBuilder.name(PRODUCT_TRIGGER_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean(name="jsonStream")
    public StreamsBuilderFactoryBean jsonStreamBuilder(){
        Map<String, Object> jsonStreamBuilderProperties = commonStreamsConfigProperties(2);
        jsonStreamBuilderProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-stream");
        return new StreamsBuilderFactoryBean(new KafkaStreamsConfiguration(jsonStreamBuilderProperties));
    }
    @Bean(name="csvStream")
    public StreamsBuilderFactoryBean csvStreamBuilder(){
        Map<String, Object> jsonStreamBuilderProperties = commonStreamsConfigProperties(4);
        jsonStreamBuilderProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-csv-stream");
        return new StreamsBuilderFactoryBean(new KafkaStreamsConfiguration(jsonStreamBuilderProperties));
    }
    @Bean(name="kTableStreamBuilder")
    public StreamsBuilderFactoryBean kTableStreamBuilder(){
        Map<String, Object> jsonStreamBuilderProperties = commonStreamsConfigProperties(1);
        jsonStreamBuilderProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-ktable-stream");
        return new StreamsBuilderFactoryBean(new KafkaStreamsConfiguration(jsonStreamBuilderProperties));
    }

    private Map<String, Object> commonStreamsConfigProperties(Integer threadcount)  {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, threadcount);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.ByteArray().getClass().getName());
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class.getName());
        props.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, DefaultProductionExceptionHandler.class.getName());
        //Must add retry. otherwise competing transaction will fail
        props.put("retries", 10);
        props.put("retry.backoff.ms", 100);
        return props;
    }
}
