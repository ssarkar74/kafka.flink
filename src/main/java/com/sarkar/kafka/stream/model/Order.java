package com.sarkar.kafka.stream.model;

public record Order(String clientId, String cusip, Long amount) {
}
