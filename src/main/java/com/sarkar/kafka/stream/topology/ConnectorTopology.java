package com.sarkar.kafka.stream.topology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sarkar.kafka.stream.dao.StoreDao;
import com.sarkar.kafka.stream.model.FileLine;
import com.sarkar.kafka.stream.transformer.DedupeDBTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.StringTokenizer;

@Slf4j
@Component
public class ConnectorTopology {
    @Autowired
    private StoreDao storeDao;
    public static class JsonSerializer<T> implements Serializer<T>{
        private final Gson gson = new GsonBuilder().create();
        @Override
        public byte[] serialize(String topic, T data) {
            return gson.toJson(data).getBytes();
        }
    }
    public static class JsonDeserializer<T> implements Deserializer<T>{
        private final Gson gson = new GsonBuilder().create();

        private Class<T> type;

        public JsonDeserializer(Class<T> type){
            this.type = type;
        }
        @Override
        public T deserialize(String topic, byte[] data) {
            return gson.fromJson(new String(data), type);
        }
    }

    final JsonSerializer<FileLine> serializer = new JsonSerializer<>();
    final JsonDeserializer<FileLine> deserializer = new JsonDeserializer<>(FileLine.class);
    Serde<FileLine> serde = Serdes.serdeFrom(serializer, deserializer);

    /*@Autowired
    public void process(@Qualifier(value = "jsonStream")StreamsBuilder streamsBuilder){
        streamsBuilder.addStateStore(dedupeStoreBuilder);
        KStream<String, FileLine> stream = streamsBuilder.stream("orders",
                Consumed.with(Serdes.String(), new JsonSerde<>(FileLine.class)))
                .selectKey((key, value) -> {
                    if(StringUtils.isBlank(value.payload())){
                        return null;
                    }
                    StringTokenizer tokens = new StringTokenizer(value.payload());
                    return tokens.nextToken();
                });
        stream.process(() -> new DedupeDBTransformer<>((key, value) -> {
            if(StringUtils.isBlank(value.payload())) {
                return null;
            }
            StringTokenizer tokens = new StringTokenizer(value.payload());
            return tokens.nextToken();
        },storeRepo));
    }*/
    @Autowired
    public void process(@Qualifier(value = "jsonStream")StreamsBuilder streamsBuilder){
        KStream<String, String> stream = streamsBuilder.stream("orders",
                        Consumed.with(Serdes.String(), Serdes.String()))
                .selectKey((key, value) -> {
                    if(StringUtils.isBlank(value)){
                        return null;
                    }
                    StringTokenizer tokens = new StringTokenizer(value);
                    return tokens.nextToken();
                });
        stream.process(() -> new DedupeDBTransformer<>((key, value) -> {
            if(StringUtils.isBlank(value)) {
                return null;
            }
            StringTokenizer tokens = new StringTokenizer(value);
            return tokens.nextToken();
        }, storeDao));
    }
}
