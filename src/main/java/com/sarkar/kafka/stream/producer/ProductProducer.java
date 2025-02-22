package com.sarkar.kafka.stream.producer;


import com.sarkar.kafka.stream.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.sarkar.kafka.stream.Constant.PRODUCT_TOPIC;
import static com.sarkar.kafka.stream.Constant.RECORDS_TO_GENERATE;

@Slf4j
public class ProductProducer {
    public static void main(String[] args){
        final Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        final KafkaProducer<String, Product> kafkaProducer = new KafkaProducer<>(producerProperties,
                        Serdes.String().serializer(), new JsonSerde<>(Product.class).serializer());
        for(long i = 0; i < RECORDS_TO_GENERATE; i++){
            final Product product = new Product("CUSIP"+i, "PRODUCT NAME " + i, "NYSE");
            log.info("Pub Product : {}", product);
            try {
                kafkaProducer.send(new ProducerRecord<>(PRODUCT_TOPIC, product.getCusip(), product)).get();
            } catch (InterruptedException e) {
                log.error("{}", e);
            } catch (ExecutionException e) {
                log.error("{}", e);
            }
        }

    }
}
