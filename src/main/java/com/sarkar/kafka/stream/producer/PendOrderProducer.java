package com.sarkar.kafka.stream.producer;

import com.sarkar.kafka.stream.model.PendOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.sarkar.kafka.stream.Constant.*;

@Slf4j
public class PendOrderProducer {
    public static KafkaProducer<String, PendOrder> kafkaProducer = new KafkaProducer<>(producerProps(),
            Serdes.String().serializer(), new JsonSerde<>(PendOrder.class).serializer());
    public static Map<String, Object> producerProps(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return props;
    }
    public static void main(String[] args){
        for(long i = 0; i < RECORDS_TO_GENERATE; i++){
            final PendOrder pendOrder = new PendOrder(i+"", "CUSIP"+i, 300*(i+1), true);
            pendProducer(pendOrder);
            log.info("Pub Order : {}", pendOrder);
        }
    }
    public static void pendProducer(PendOrder pendOrder) {
        try {
            kafkaProducer.send(new ProducerRecord<>(ORDER_TOPIC, pendOrder.clientId(), pendOrder)).get();
        } catch (InterruptedException e) {
            log.error("{}", e);
        } catch (ExecutionException e) {
            log.error("{}", e);
        }
    }
}
