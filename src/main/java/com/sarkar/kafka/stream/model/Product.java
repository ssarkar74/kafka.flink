package com.sarkar.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.ToString;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "cusip",
        "name",
        "exchange"
})
@ToString
public class Product {
    @JsonProperty("cusip")
    String cusip;
    @JsonProperty("name")
    String name;
    @JsonProperty("exchange")
    String exchange;

    public Product() {
        super();
    }

    public Product(String cusip, String name, String exchange) {
        this.cusip = cusip;
        this.name = name;
        this.exchange = exchange;
    }

    @JsonProperty("cusip")
    public String getCusip() {
        return cusip;
    }
    @JsonProperty("cusip")
    public void setCusip(String cusip) {
        this.cusip = cusip;
    }
    @JsonProperty("name")
    public String getName() {
        return name;
    }
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }
    @JsonProperty("exchange")
    public String getExchange() {
        return exchange;
    }
    @JsonProperty("exchange")
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(cusip, product.cusip) && Objects.equals(name, product.name) && Objects.equals(exchange, product.exchange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cusip, name, exchange);
    }
}
