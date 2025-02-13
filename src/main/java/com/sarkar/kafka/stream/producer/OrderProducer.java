package com.sarkar.kafka.stream.producer;

import com.sarkar.kafka.stream.model.Client;
import com.sarkar.kafka.stream.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.jackson.JsonObjectSerializer;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.sarkar.kafka.stream.Constant.*;

@Slf4j
public class OrderProducer {
    public static void main(String[] args){
        final Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        KafkaProducer<String, Order> kafkaProducer = new KafkaProducer<>(producerProperties,
                Serdes.String().serializer(), new JsonSerde<>(Order.class).serializer());
        for(long i = 0; i < RECORDS_TO_GENERATE; i++){
            final Order order = new Order(i+"", "CUSIP"+i, 300*i);
            log.info("Pub Order : {}", order);
           try {
                kafkaProducer.send(new ProducerRecord<>(ORDER_TOPIC, order.clientId(), order)).get();
            } catch (InterruptedException e) {
                log.error("{}", e);
            } catch (ExecutionException e) {
                log.error("{}", e);
            }
        }

    }
}
