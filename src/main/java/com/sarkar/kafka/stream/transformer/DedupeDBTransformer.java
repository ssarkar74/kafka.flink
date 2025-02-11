package com.sarkar.kafka.stream.transformer;

import com.sarkar.kafka.stream.dao.StoreDao;
import com.sarkar.kafka.stream.entity.Store;
import com.sarkar.kafka.stream.exception.DataException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
public class DedupeDBTransformer<K, V, E> implements Processor<K, V, K, V> {
    private static final Logger log = LoggerFactory.getLogger(DedupeDBTransformer.class);
    private ProcessorContext context;

    private StoreDao storeDao;
    /*
     * Key: unique ID
     * Value: time stamp (event time ) of the corresponding event
     * when the key was seen for the first time
     */
    private final KeyValueMapper<K, V, E> idExtractor;

    /**
     * @param idExtractor                   extracts a unique id from a record by which de-duplicate
     *                                      the input records.
     *
     *                                      If it returns null,
     *                                      the records will be considered for de-duping
     *                                      and forwarded as-is
     */
    public DedupeDBTransformer(final KeyValueMapper<K, V, E> idExtractor,
                               final StoreDao storeDao) {
        this.idExtractor = idExtractor;
        this.storeDao = storeDao;
    }
    @Override
    public void init(ProcessorContext<K, V> context) {
        this.context = context;
    }
    @Override
    public void process(Record<K, V> record) {

        final E eventId = idExtractor.apply(record.key(), record.value());
        log.info("eventId : {} Key : {} - Value : {}", eventId, record.key(), record.value());

        if(eventId == null) {
            context.forward(record);
        } else {
            final KeyValue<K, V> output;
            Store store = this.storeDao.getStoreByEventId(eventId.toString());
            if (store == null) {
                LocalDateTime localDateTime =
                        Instant.ofEpochMilli(context.currentStreamTimeMs())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                store = Store.builder().eventId(eventId.toString()).updateTime(localDateTime).build();
                log.info("Not Duplicate.......");
                try {
                    this.storeDao.insert(store);
                } catch (DataIntegrityViolationException e) {
                    log.info("Duplicate by DB constraint.......");
                }
            } else {
                output = KeyValue.pair(record.key(), record.value());
                LocalDateTime localDateTime =
                        Instant.ofEpochMilli(context.currentStreamTimeMs())
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime();
                log.info("Duplicate.......");
                this.storeDao.updateTimeStamp(store);
            }
            context.forward(record);
        }
    }

    @Override
    public void close() {
        //Note: The store should not be closed manually here via eventId.close()
        // The Kafka Streams API will automatically close the stores when necessary
    }
}
