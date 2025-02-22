package com.sarkar.kafka.stream.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarkar.kafka.stream.Constant;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {
    private ObjectMapper objectMapper = new ObjectMapper();
    private Class<T> className;

    public JsonDeserializer() {
        super();
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.className = (Class<T>) configs.get(Constant.SPECIFIC_CLASS_NAME);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        if(data == null) {
            return null;
        }
        try {
            return this.objectMapper.readValue(data, className);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public void close() {
        //nothign to close
    }
}
