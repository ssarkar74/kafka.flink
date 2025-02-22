package com.sarkar.kafka.stream.topology;


import com.sarkar.kafka.stream.model.*;
import com.sarkar.kafka.stream.producer.PendOrderProducer;
import com.sarkar.kafka.stream.serde.AppSerdes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sarkar.kafka.stream.Constant.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendTopology {
    private final GlobalKTable<String, Client> clients;
    private final GlobalKTable<String, Product> products;
    private final KTable<String, PendOrder> pendOrders;
    private final KStream<String, Client> triggerClients;

    @Bean
    public void processClientTrigger() {

        KTable<String, ClientOrders> clientBasedPedOrders = pendOrders.toStream()
                .groupBy(
                        (orderId, pendOrder) -> pendOrder.getClientId(),
                        Grouped.with(AppSerdes.String(), AppSerdes.PendOrder()))
                .aggregate(() -> new ClientOrders(),
                        (clientId, pendOrder, clientOrders) ->
                                clientOrders.withClientOrder(new ClientOrder().withOrder(pendOrder)),
                        Materialized.with(AppSerdes.String(), AppSerdes.ClientOrders()));
        clientBasedPedOrders.toStream().peek((k, v) -> log.info("clientBasedPedOrders key : {} value : {}", k, v));

        KStream<String, ClientOrders> pendClientOrders = triggerClients.join(clientBasedPedOrders,
                (client, clientOrders) -> filterClientOrders(client, clientOrders));
        pendClientOrders.peek((k, v) -> log.info("pendClientOrders key : {} value : {}", k, v));

        replublish(pendClientOrders);
    }

    private void replublish(KStream<String, ClientOrders> pendClientOrders) {
        KStream<String, ClientOrder> orderIdBasedOrders = pendClientOrders
                .flatMap((clientId, clientOrders) -> getFlatClientOrders(clientOrders));
        orderIdBasedOrders.peek((k, v) -> log.info("orderIdBasedOrders key : {} value : {}", k, v));
        orderIdBasedOrders.join(products,
                        (orderId, clientOrder) -> clientOrder.getOrder().getCusip(),
                        (clientOrder, product) ->
                                new EnrichedOrder(clientOrder.getClient(), product, clientOrder.getOrder()))
                .peek((orderId, enrichedOrder) -> log.info("Enriched - Key : {}, Value : {} ", orderId, enrichedOrder))
                .peek((orderId, enrichedOrder) -> republish(enrichedOrder));
    }

    private static void republish(EnrichedOrder enrichedOrder){
        PendOrder  pendOrder = new PendOrder(enrichedOrder.getPendOrder().getOrderId(),
                enrichedOrder.getClient().getId(),
                enrichedOrder.getProduct().getCusip(),
                enrichedOrder.getPendOrder().getAmount(),
                false);
        PendOrderProducer.pendProducer(pendOrder);

    }
    private static List<KeyValue<String, ClientOrder>> getFlatClientOrders(ClientOrders clientOrders) {
        List<KeyValue<String, ClientOrder>> keyValuePairs = new ArrayList<>();
        clientOrders.getClientOrderSet().forEach(clientOrder ->
                keyValuePairs.add(new KeyValue<>(clientOrder.getOrder().getOrderId(), clientOrder)));
        return keyValuePairs;
    }

    private static ClientOrders filterClientOrders(Client client, ClientOrders clientOrders) {
        new ClientOrders().withClientOrders(clientOrders.getClientOrderSet().stream()
                .map(clientOrder -> clientOrder.withClient(client))
                .filter(clientOrder -> clientOrder.getOrder().getPend())
                .collect(Collectors.toSet()));
        Set<ClientOrder> orderSet = new HashSet<>();
        for (ClientOrder clientOrder : clientOrders.getClientOrderSet()) {
            if (clientOrder.getOrder().getPend()) {
                orderSet.add(clientOrder.withClient(client));
            }
        }
        ClientOrders newClientOrders = new ClientOrders();
        newClientOrders.setClientOrderSet(orderSet);
        return newClientOrders;
    }
}