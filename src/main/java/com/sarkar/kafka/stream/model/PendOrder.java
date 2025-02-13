package com.sarkar.kafka.stream.model;

public record PendOrder(String clientId, String cusip, Long amount, Boolean isPend) {
}
