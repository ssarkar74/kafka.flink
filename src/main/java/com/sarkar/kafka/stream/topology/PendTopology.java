package com.sarkar.kafka.stream.topology;


import com.sarkar.kafka.stream.model.Client;
import com.sarkar.kafka.stream.model.Order;
import com.sarkar.kafka.stream.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import static com.sarkar.kafka.stream.Constant.*;

@Slf4j
@Component
public class PendTopology {
    @Autowired
    public void processCsv(@Qualifier(value = "kTableStreamBuilder") StreamsBuilder streamsBuilder){
        final GlobalKTable<String, Client> clients = streamsBuilder.globalTable(CLIENT_TOPIC,
                Consumed.with(Serdes.String(), new JsonSerde<>(Client.class)));
        final GlobalKTable<String, Product> products = streamsBuilder.globalTable(PRODUCT_TOPIC,
                Consumed.with(Serdes.String(), new JsonSerde<>(Product.class)));
        final KTable<String, Order> orders = streamsBuilder.table(ORDER_TOPIC,
                Consumed.with(Serdes.String(), new JsonSerde<>(Order.class)));
        final KStream<String, Client> clientStream = streamsBuilder.stream(CLIENT_TRIGGER_TOPIC,
                Consumed.with(Serdes.String(), new JsonSerde<>(Client.class)));
        final KStream<String, ClientOrder> clientOrders = clientStream
                .join(orders, (client,order) -> new ClientOrder(client, order));
        final KStream<String, EnrichedOrder> enrichedOrders = clientOrders
                .join(products, (orderId, clientOrder) -> clientOrder.order().cusip(),
                        (clientOrder, product) -> new EnrichedOrder(clientOrder.client, product, clientOrder.order));
        enrichedOrders.foreach((key, amount) ->
                log.info("Client : {}, Product : {} Order : {}, ", amount.client, amount.product, amount.order));
    }

   private record ClientOrder(Client client, Order order){
   }
   private record EnrichedOrder(Client client, Product product, Order order){
   }
}
