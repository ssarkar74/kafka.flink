package com.sarkar.kafka.stream.producer;

import com.sarkar.kafka.stream.model.Client;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.sarkar.kafka.stream.Constant.CLIENT_TOPIC;
import static com.sarkar.kafka.stream.Constant.RECORDS_TO_GENERATE;
@Slf4j
public class ClientProducer {
    public static void main(String[] args){
        final Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        final KafkaProducer<String, Client> kafkaProducer = new KafkaProducer<>(producerProperties,
                        Serdes.String().serializer(), new JsonSerde<>(Client.class).serializer());
        for(long i = 0; i < RECORDS_TO_GENERATE; i++){
            final Client client = new Client(i+"", "Fund Client - " + i, "Hedge Fund", System.currentTimeMillis());
            log.info("Pub Client : {}", client);
            try {
                kafkaProducer.send(new ProducerRecord<>(CLIENT_TOPIC, client.getId(), client)).get();
            } catch (InterruptedException e) {
                log.error("{}", e);
            } catch (ExecutionException e) {
                log.error("{}", e);
            }
        }

    }
}
