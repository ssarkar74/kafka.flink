package com.sarkar.kafka.stream.serde;

import com.sarkar.kafka.stream.Constant;
import com.sarkar.kafka.stream.model.*;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;

public class AppSerdes extends Serdes {
    public static final class ClientSerde extends WrapperSerde<Client> {
        public ClientSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }
    public static Serde<Client> Client(){
        ClientSerde serde = new ClientSerde();
        Map<String, Object> serdesConfig = new HashMap<>();
        serdesConfig.put(Constant.SPECIFIC_CLASS_NAME, Client.class);
        serde.configure(serdesConfig, false);
        return serde;
    }

    public static final class ProductSerde extends WrapperSerde<Product> {
        public ProductSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }
    public static Serde<Product> Product(){
        ProductSerde serde = new ProductSerde();
        Map<String, Object> serdesConfig = new HashMap<>();
        serdesConfig.put(Constant.SPECIFIC_CLASS_NAME, Product.class);
        serde.configure(serdesConfig, false);
        return serde;
    }

    public static final class PendOrderSerde extends WrapperSerde<PendOrder> {
        public PendOrderSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }
    public static Serde<PendOrder> PendOrder(){
        PendOrderSerde serde = new PendOrderSerde();
        Map<String, Object> serdesConfig = new HashMap<>();
        serdesConfig.put(Constant.SPECIFIC_CLASS_NAME, PendOrder.class);
        serde.configure(serdesConfig, false);
        return serde;
    }

    public static final class ClientOrderSerde extends WrapperSerde<ClientOrder> {
        public ClientOrderSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }
    public static Serde<ClientOrder> ClientOrder(){
        ClientOrderSerde serde = new ClientOrderSerde();
        Map<String, Object> serdesConfig = new HashMap<>();
        serdesConfig.put(Constant.SPECIFIC_CLASS_NAME, ClientOrder.class);
        serde.configure(serdesConfig, false);
        return serde;
    }

    public static final class ClientOrdersSerde extends WrapperSerde<ClientOrders> {
        public ClientOrdersSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }
    public static Serde<ClientOrders> ClientOrders(){
        ClientOrdersSerde serde = new ClientOrdersSerde();
        Map<String, Object> serdesConfig = new HashMap<>();
        serdesConfig.put(Constant.SPECIFIC_CLASS_NAME, ClientOrders.class);
        serde.configure(serdesConfig, false);
        return serde;
    }

    public static final class EnrichedOrderSerde extends WrapperSerde<EnrichedOrder> {
        public EnrichedOrderSerde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>());
        }
    }
    public static Serde<EnrichedOrder> EnrichedOrder(){
        EnrichedOrderSerde serde = new EnrichedOrderSerde();
        Map<String, Object> serdesConfig = new HashMap<>();
        serdesConfig.put(Constant.SPECIFIC_CLASS_NAME, EnrichedOrder.class);
        serde.configure(serdesConfig, false);
        return serde;
    }

}
