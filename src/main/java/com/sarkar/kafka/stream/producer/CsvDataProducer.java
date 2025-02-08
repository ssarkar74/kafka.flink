package com.sarkar.kafka.stream.producer;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
@Slf4j
public class CsvDataProducer {
    public static void  main(String[] args){
        publishOrders(buildOrders());
    }
    private static List<String> buildOrders(){
        var order1 = "6542, product_6534, 27.00";
        var order2 = "6542, product_6534, 27.00";
        var order3 = "1234, product_6534, 27.00";
        var order4 = "1234, product_6534, 27.00";
        return List.of(order1, order2, order3, order4);
    }
    private static void publishOrders(List<String> orders){
        AtomicInteger i = new AtomicInteger(0);
        orders.forEach(order -> {
            var recordMetadata = ProducerUtil.publishMessage("csv-orders", i.addAndGet(1) + "", order);
            log.info("{}", recordMetadata);
        });
    }
}
