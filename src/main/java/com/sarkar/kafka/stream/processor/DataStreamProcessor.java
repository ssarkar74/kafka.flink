package com.sarkar.kafka.stream.processor;

import com.sarkar.kafka.stream.model.Client;
import com.sarkar.kafka.stream.model.ClientOrder;
import com.sarkar.kafka.stream.model.PendOrder;
import com.sarkar.kafka.stream.processor.function.ClientFunction;
import com.sarkar.kafka.stream.processor.function.PendOrderFunction;
import com.sarkar.kafka.stream.processor.joiner.ClientOrderJoiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class DataStreamProcessor {
    @Value("${spring.kafka.streams.bootstrap-servers}")
    private String bootstrap;
    private final StreamExecutionEnvironment env;
    private final PendOrderFunction pendOrderFunction;
    private final ClientFunction clientFunction;

    @Bean
    public void processOrders() throws Exception{
        //1. Create sources
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(bootstrap)
                .setTopics("order_flink_topic1")
                .setGroupId("flink-join-group1")
                .setProperty("client.id", "order-source-" + UUID.randomUUID())
                .setProperty("metrics.reporters", "")
                .setProperty("enable.auto.commit", "false")
                .setStartingOffsets(OffsetsInitializer.latest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        KafkaSource<String> customerSource = KafkaSource.<String>builder()
                .setBootstrapServers(bootstrap)
                .setTopics("client_flink_topic1")
                .setGroupId("flink-join-group1")
                .setProperty("client.id", "client-source-" + UUID.randomUUID())
                .setProperty("metrics.reporters", "")
                .setProperty("enable.auto.commit", "false")
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        //2. Process streams
        DataStream<Client> clients = env.fromSource(customerSource, WatermarkStrategy.noWatermarks(), "Kafka Clients")
                .process(new ClientFunction())
                .returns(Client.class);
        DataStream<PendOrder> orders = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Orders")
                .process(new PendOrderFunction())
                .returns(PendOrder.class);

        //3. Key both streams by their common id
        KeyedStream<PendOrder, String> keyedOrders = orders.keyBy(PendOrder::getClientId);
        KeyedStream<Client, String> keyedClients = clients.keyBy(Client::getId);
        //4. Join using CoProcessFunction
        DataStream<ClientOrder> joinedStream = keyedClients.connect(keyedOrders)
                .process(new ClientOrderJoiner());
        joinedStream.print();
        env.execute();
    }
}
