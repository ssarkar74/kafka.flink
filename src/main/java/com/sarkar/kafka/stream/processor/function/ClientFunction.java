package com.sarkar.kafka.stream.processor.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.sarkar.kafka.stream.model.Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
@Component
public class ClientFunction extends ProcessFunction<String, Client>
        implements Serializable {
    private static final long serialVersionUID=122334L;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Override
    public void processElement(String value, ProcessFunction<String, Client>.Context context, Collector<Client> collector) throws Exception {
        try {
            log.info("CLIENT : {}", value);
            JsonNode node = objectMapper().readTree(value);
            if(node.isObject()){
                Client client = objectMapper().treeToValue(node, Client.class);
                collector.collect(client);
            } else {
                log.warn("non-object JSON received : {}", value);
            }
        } catch (MismatchedInputException e) {
            log.warn("SKIPPING Value : {}", value);
        }
    }
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
                /*.registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setDateFormat(new StdDateFormat()
                        .withColonInTimeZone(true));*/
    }
}
