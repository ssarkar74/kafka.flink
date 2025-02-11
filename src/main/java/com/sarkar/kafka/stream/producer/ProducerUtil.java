package com.sarkar.kafka.stream.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
public class ProducerUtil {
    public static KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(producerProps());
    public static Map<String, Object> producerProps(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return props;
    }

    public static RecordMetadata publishMessage(String topicName, String key, String message){
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topicName, Integer.valueOf(key) % 4, key, message);
        RecordMetadata recordMetadata = null;
            log.info("producer record : {}", producerRecord);
        try {
            recordMetadata = kafkaProducer.send(producerRecord).get();
        } catch (InterruptedException e) {
            log.error("Error", e);
        } catch (ExecutionException e) {
            log.error("Error", e);
        }
        return  recordMetadata;
    }
}
