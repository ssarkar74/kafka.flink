package com.sarkar.kafka.stream.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static com.sarkar.kafka.stream.Constant.*;

@Slf4j
@Configuration
public class KafkaConfig {
   @Value("${spring.kafka.streams.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${flink.group-id}")
    private String flinkGroupId;
    @Value("${flink.auto-oofset-reset}")
    private String autoOffsetReset;

    /*@Bean
    public KafkaSource<String> orderSource() {
        return KafkaSource.<String>builder()
                .setBootstrapServers(bootstrapServers)
                .setTopics("order-flink-topic1")
                .setGroupId(flinkGroupId)
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .setStartingOffsets(getOffsetIntializer(autoOffsetReset))
                .build();
    }
    private OffsetsInitializer getOffsetIntializer(String policy){
        return switch(policy.toLowerCase()) {
            case "earliest" -> OffsetsInitializer.earliest();
            case "latest" -> OffsetsInitializer.latest();
            default -> OffsetsInitializer.committedOffsets(OffsetResetStrategy.valueOf(policy.toUpperCase()));
        };
    }*/
    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setDateFormat(new StdDateFormat()
                        .withColonInTimeZone(true));
    }

    @Bean
    public StreamExecutionEnvironment env(){
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
                3, //number of restart attempts
                Time.of(10, TimeUnit.SECONDS))); //delay
        env.setParallelism(1);
        env.enableCheckpointing(1000000, CheckpointingMode.EXACTLY_ONCE);
        return env;
    }

}
