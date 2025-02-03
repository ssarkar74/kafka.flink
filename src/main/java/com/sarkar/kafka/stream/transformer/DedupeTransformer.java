package com.sarkar.kafka.stream.transformer;

import com.sarkar.kafka.stream.entity.Store;
import com.sarkar.kafka.stream.repository.StoreRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.WindowStore;
import org.apache.kafka.streams.state.WindowStoreIterator;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class DedupeTransformer<K, V, E> implements Transformer<K, V, KeyValue<K, V>> {
    private ProcessorContext context;
    public static final String STORE_NAME = "csv-store";

    private StoreRepo storeRepo;
    /*
     * Key: unique ID
     * Value: time stamp (event time ) of the corresponding event
     * when the key was seen for the first time
     */
    private WindowStore<E, Long> windowStore;
    private final long leftDuraionMs;
    private final long rightDurationMs;
    private final KeyValueMapper<K, V, E> isExtractor;

    /**
     *
     * @param maintainDurationPerEventInMs  how ong to remember a known event,
     *                                      during the time of which any incoming duplicates
     *                                      of the event will be dropped
     * @param idExtractor                   extracts a unique id from a record by which de-duplicate
     *                                      the input records.
     *
     *                                      If it returns null,
     *                                      the records will be considered for de-duping
     *                                      and forwarded as-is
     */
    public DedupeTransformer(final long maintainDurationPerEventInMs,
                             final KeyValueMapper<K, V, E> idExtractor) {
        leftDuraionMs = maintainDurationPerEventInMs / 2;
        rightDurationMs = maintainDurationPerEventInMs - leftDuraionMs;
        this.isExtractor = idExtractor;
    }
    @Autowired
    public void init(final ProcessorContext processorContext) {
        this.context = processorContext;
        windowStore = context.getStateStore(STORE_NAME);
    }

    @Override
    public KeyValue<K, V> transform(final K key, final V value) {
        return windowStoreBasedDuplication(key, value);
    }

    private  KeyValue<K, V> windowStoreBasedDuplication(K key, V value) {
        final E eventId = isExtractor.apply(key, value);
        log.info("eventId : {} Key : {} - Value : {}", eventId, key, value);

        if(eventId == null) {
            return KeyValue.pair(key, value);
        } else {
            final KeyValue<K, V> output;
            if (isDuplicate(eventId)) {
                output = null;
                updateTimestampOfExistingEventToPreventExpiry(eventId, context.timestamp());
            } else {
                output = KeyValue.pair(key, value);
                rememberNewEvent(eventId, context.timestamp());
            }
            return output;
        }
    }
    private  KeyValue<K, V> dbStoreBasedDuplication(K key, V value) {
        final E eventId = isExtractor.apply(key, value);
        log.info("eventId : {} Key : {} - Value : {}", eventId, key, value);
        if(eventId == null) {
            return KeyValue.pair(key, value);
        } else {
            final KeyValue<K, V> output;

            if (isDuplicate(eventId)) {
                output = null;
                updateTimestampOfExistingEventToPreventExpiry(eventId, context.timestamp());
            } else {
                output = KeyValue.pair(key, value);
                rememberNewEvent(eventId, context.timestamp());
            }
            return output;
        }
    }
    private void rememberNewEvent(E eventId, long timestamp) {
        windowStore.put(eventId, timestamp, timestamp);
    }

    private void updateTimestampOfExistingEventToPreventExpiry(E eventId, long newTimestamp) {
        windowStore.put(eventId, newTimestamp, newTimestamp);
    }

    private boolean isDuplicate(E eventId) {
        final long eventTime = context.timestamp();
        final WindowStoreIterator<Long> timeIterator = windowStore.fetch(
                eventId,
                eventTime - leftDuraionMs,
                eventTime + rightDurationMs);
        final  boolean isDuplicate = timeIterator.hasNext();
        timeIterator.close();
        log.info("For event : {} duplicate flag : {}", eventId, isDuplicate);
        return isDuplicate;

    }

    @Override
    public void close() {
        //Note: The store should not be closed manually here via eventId.close()
        // The Kafka Streams API will automatically close the stores when necessary
    }
}
