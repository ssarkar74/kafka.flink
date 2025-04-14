package com.sarkar.kafka.stream.processor.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sarkar.kafka.stream.model.PendOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Slf4j
@RequiredArgsConstructor
@Component
public class PendOrderFunction extends ProcessFunction<String, PendOrder>
        implements Serializable {
    private static final long serialVersionUID=123354L;

    @Override
    public void processElement(String value, ProcessFunction<String, PendOrder>.Context context, Collector<PendOrder> collector) throws Exception {
        try {
            log.info("ORDER : {}", value);
            JsonNode node = objectMapper().readTree(value);
            if(node.isObject()){
                PendOrder order = objectMapper().treeToValue(node, PendOrder.class);
                collector.collect(order);
            } else {
                log.warn("non-object JSON received : {}", value);
            }
        } catch (MismatchedInputException e) {
            log.warn("SKIPPING Value : {}", value);
        }
    }
    public ObjectMapper objectMapper(){
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setDateFormat(new StdDateFormat()
                        .withColonInTimeZone(true));
    }
}
