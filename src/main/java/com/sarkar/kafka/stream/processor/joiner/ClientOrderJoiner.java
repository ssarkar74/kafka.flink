package com.sarkar.kafka.stream.processor.joiner;

import com.sarkar.kafka.stream.model.Client;
import com.sarkar.kafka.stream.model.ClientOrder;
import com.sarkar.kafka.stream.model.PendOrder;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.util.Collector;

@Slf4j
public class ClientOrderJoiner extends CoProcessFunction<Client, PendOrder, ClientOrder> {
    private ListState<PendOrder> orderState;
    private ValueState<Client> clientState;

    @Override
    public void open(Configuration configuration){
        orderState = getRuntimeContext().getListState(
                new ListStateDescriptor<>("orderState", PendOrder.class));
        clientState = getRuntimeContext().getState(
                new ValueStateDescriptor<>("clientState", Client.class));
    }

    @Override
    public void processElement1(Client client, CoProcessFunction<Client, PendOrder, ClientOrder>.Context context, Collector<ClientOrder> collector) throws Exception {
        //1. Store the client
        clientState.update(client);

        //2. Check if we have any orders
        Iterable<PendOrder> orders = orderState.get();
        log.info("processElement1...... client: {} order: {}", client, orders);
        if(orders.iterator() != null && orders.iterator().hasNext()){
            collector.collect(createResult(client, orders.iterator().next()));
        }
        //3. Clear order after emitting
        orderState.clear();
    }

    @Override
    public void processElement2(PendOrder order, CoProcessFunction<Client, PendOrder, ClientOrder>.Context context, Collector<ClientOrder> collector) throws Exception {
        Client client = clientState.value();
        if(client != null) {
            log.info("processElement2...... client: {} order: {}", client, order);
            //1. Add order to orderState
            orderState.add(order);
            //2. Get all orders so far
            Iterable<PendOrder> orders = orderState.get();
            if(orders.iterator() != null && orders.iterator().hasNext()){
                collector.collect(createResult(client, orders.iterator().next()));
            }
            orderState.clear();
        } else {
            orderState.add(order);
        }
    }
    private ClientOrder createResult(Client client, PendOrder order){
        return new ClientOrder(client, order);
    }
}
