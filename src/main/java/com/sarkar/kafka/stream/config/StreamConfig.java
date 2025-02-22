package com.sarkar.kafka.stream.config;

import com.sarkar.kafka.stream.model.Client;
import com.sarkar.kafka.stream.model.PendOrder;
import com.sarkar.kafka.stream.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.DefaultProductionExceptionHandler;
import org.apache.kafka.streams.errors.LogAndContinueExceptionHandler;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;

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
        props.put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndContinueExceptionHandler.class.getName());
        props.put(StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG, DefaultProductionExceptionHandler.class.getName());
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 10485760L); //10MB
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000); //1 second
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000); //30seconds
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 10000); //10seconds


        //Must add retry. otherwise competing transaction will fail
        props.put("retries", 10);
        props.put("retry.backoff.ms", 100);
        return props;
    }
    @Bean
    public GlobalKTable<String, Client> clients(@Qualifier(value = "kTableStreamBuilder") StreamsBuilder streamsBuilder){
        return streamsBuilder.globalTable(CLIENT_TOPIC,
                Consumed.with(Serdes.String(), new JsonSerde<>(Client.class)));
    }
    @Bean
    public GlobalKTable<String, Product> products(@Qualifier(value = "kTableStreamBuilder") StreamsBuilder streamsBuilder){
       return streamsBuilder.globalTable(PRODUCT_TOPIC,
                Consumed.with(Serdes.String(), new JsonSerde<>(Product.class)));
    }
    @Bean
    public KTable<String, PendOrder> pendOrders(@Qualifier(value = "kTableStreamBuilder") StreamsBuilder streamsBuilder){
        return streamsBuilder.table(ORDER_TOPIC,
                Consumed.with(Serdes.String(), new JsonSerde<>(PendOrder.class)));
    }
    @Bean
    public KStream<String, Client> triggerClients(@Qualifier(value = "kTableStreamBuilder") StreamsBuilder streamsBuilder){
        return streamsBuilder.stream(CLIENT_TRIGGER_TOPIC,
                Consumed.with(Serdes.String(), new JsonSerde<>(Client.class)));
    }
}
