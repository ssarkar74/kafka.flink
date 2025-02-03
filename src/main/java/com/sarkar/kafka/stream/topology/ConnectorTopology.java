package com.sarkar.kafka.stream.topology;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sarkar.kafka.stream.entity.Store;
import com.sarkar.kafka.stream.model.FileLine;
import com.sarkar.kafka.stream.repository.StoreRepo;
import com.sarkar.kafka.stream.transformer.DedupeDBTransformer;
import com.sarkar.kafka.stream.transformer.DedupeTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.StringTokenizer;

import static com.sarkar.kafka.stream.transformer.DedupeTransformer.STORE_NAME;

@Slf4j
@Component
public class ConnectorTopology {
    @Autowired
    private StoreRepo storeRepo;
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

    final StoreBuilder<WindowStore<FileLine, Long>> dedupeStoreBuilder = Stores.windowStoreBuilder(
            Stores.persistentWindowStore(STORE_NAME,
                    Duration.ofDays(1),
                    Duration.ofDays(1),
                    false),
            serde,
            Serdes.Long());
    @Autowired
    public void process(StreamsBuilder streamsBuilder){
        streamsBuilder.addStateStore(dedupeStoreBuilder);
        KStream<String, FileLine> stream = streamsBuilder.stream("orders",
                Consumed.with(Serdes.String(), new JsonSerde<>(FileLine.class)))
                .selectKey((key, value) -> {
                    StringTokenizer tokens = new StringTokenizer(value.payload());
                    return tokens.nextToken();
                });
        stream.transform(() -> new DedupeDBTransformer<>((key, value) -> {
            if(value.payload() == null) {
                return null;
            }
            StringTokenizer tokens = new StringTokenizer(value.payload());
            return tokens.nextToken();
        },storeRepo));
        /*stream.transform(() -> new DedupeTransformer<>(Duration.ofDays(1).toMillis(),
                (key, value) -> {
                    if(value.payload() == null) {
                        return null;
                    }
                    StringTokenizer tokens = new StringTokenizer(value.payload());
                    return tokens.nextToken();
                }), STORE_NAME);*/
    }
}
