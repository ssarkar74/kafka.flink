package com.sarkar.kafka.stream.producer;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
@Slf4j
public class CsvDataProducer {
    private static final Logger log = LoggerFactory.getLogger(CsvDataProducer.class);

    public static void  main(String[] args){
        publishOrders(buildOrders());
    }
    private static List<String> buildOrders(){
        var order1 = "6542, product_6534, 27.00";
        var order2 = "6542, product_6534, 27.00";
        var order3 = "1234, product_6534, 27.00";
        var order4 = "1234, product_6534, 27.00";
        var order5 = "1234, product_6534, 27.00";
        var order6 = "1234, product_6534, 27.00";
        var order7 = "1234, product_6534, 27.00";
        var order8 = "1234, product_6534, 27.00";
        var order9 = "1234, product_6534, 27.00";
        return List.of(order1, order2, order3, order4, order5, order6, order7, order8, order9);
        //return List.of(order1);
    }
    private static void publishOrders(List<String> orders){
        AtomicInteger i = new AtomicInteger(0);
        orders.forEach(order -> {
            var recordMetadata = ProducerUtil.publishMessage("csv-orders", i.addAndGet(1) + "", order);
            log.info("{}", recordMetadata);
        });
    }
}
