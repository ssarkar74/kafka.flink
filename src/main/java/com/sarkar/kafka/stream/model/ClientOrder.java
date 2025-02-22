package com.sarkar.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.ToString;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "client",
        "order"
})
@ToString
public class ClientOrder {
    @JsonProperty("client")
    Client client;
    @JsonProperty("order")
    PendOrder order;

    @JsonProperty("client")
    public Client getClient() {
        return client;
    }
    @JsonProperty("client")
    public void setClient(Client client) {
        this.client = client;
    }
    public ClientOrder withClient(Client client){
        this.client = client;
        return this;
    }
    @JsonProperty("order")
    public PendOrder getOrder() {
        return order;
    }
    @JsonProperty("order")
    public void setOrder(PendOrder order) {
        this.order = order;
    }
    public ClientOrder withOrder(PendOrder order){
        this.order = order;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClientOrder that = (ClientOrder) o;
        return Objects.equals(client, that.client) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, order);
    }
}
