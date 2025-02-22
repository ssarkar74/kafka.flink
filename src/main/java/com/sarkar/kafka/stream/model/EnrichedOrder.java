package com.sarkar.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.ToString;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "client",
        "product",
        "pendOrder"
})
@ToString
public class EnrichedOrder {
    @JsonProperty("client")
    Client client;
    @JsonProperty("product")
    Product product;
    @JsonProperty("pendOrder")
    PendOrder pendOrder;

    public EnrichedOrder() {
        super();
    }

    public EnrichedOrder(Client client, Product product, PendOrder pendOrder) {
        this.client = client;
        this.product = product;
        this.pendOrder = pendOrder;
    }

    @JsonProperty("client")
    public Client getClient() {
        return client;
    }
    @JsonProperty("client")
    public void setClient(Client client) {
        this.client = client;
    }
    @JsonProperty("product")
    public Product getProduct() {
        return product;
    }
    @JsonProperty("product")
    public void setProduct(Product product) {
        this.product = product;
    }
    @JsonProperty("pendOrder")
    public PendOrder getPendOrder() {
        return pendOrder;
    }
    @JsonProperty("pendOrder")
    public void setPendOrder(PendOrder pendOrder) {
        this.pendOrder = pendOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EnrichedOrder that = (EnrichedOrder) o;
        return Objects.equals(client, that.client) && Objects.equals(product, that.product) && Objects.equals(pendOrder, that.pendOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, product, pendOrder);
    }
}
