package com.sarkar.kafka.stream.transformer;

import com.sarkar.kafka.stream.entity.Store;
import com.sarkar.kafka.stream.repository.StoreRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
public class DedupeDBTransformer<K, V, E> implements Transformer<K, V, KeyValue<K, V>> {
    private ProcessorContext context;

    private StoreRepo  storeRepo;
    /*
     * Key: unique ID
     * Value: time stamp (event time ) of the corresponding event
     * when the key was seen for the first time
     */
    private final KeyValueMapper<K, V, E> isExtractor;

    /**
     * @param idExtractor                   extracts a unique id from a record by which de-duplicate
     *                                      the input records.
     *
     *                                      If it returns null,
     *                                      the records will be considered for de-duping
     *                                      and forwarded as-is
     */
    public DedupeDBTransformer(final KeyValueMapper<K, V, E> idExtractor,
                               final StoreRepo storeRepo) {
        this.isExtractor = idExtractor;
        this.storeRepo = storeRepo;
    }
    @Autowired
    public void init(final ProcessorContext processorContext) {
        this.context = processorContext;
    }

    @Override
    public KeyValue<K, V> transform(final K key, final V value) {
        final E eventId = isExtractor.apply(key, value);
        log.info("eventId : {} Key : {} - Value : {}", eventId, key, value);

        if(eventId == null) {
            return KeyValue.pair(key, value);
        } else {
            final KeyValue<K, V> output;
            Store store = this.storeRepo.findByEventId(eventId.toString());
            if (store == null) {
                output = null;
                LocalDateTime localDateTime =
                        Instant.ofEpochMilli(context.timestamp())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                store = Store.builder().eventId(eventId.toString()).updateTime(localDateTime).build();
                this.storeRepo.save(store);
            } else {
                output = KeyValue.pair(key, value);
                LocalDateTime localDateTime =
                        Instant.ofEpochMilli(context.timestamp())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                //store.setUpdateTime(localDateTime);
                this.storeRepo.save(store);
            }
            return output;
        }
    }

    @Override
    public void close() {
        //Note: The store should not be closed manually here via eventId.close()
        // The Kafka Streams API will automatically close the stores when necessary
    }
}
