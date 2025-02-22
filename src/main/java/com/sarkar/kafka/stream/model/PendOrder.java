package com.sarkar.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.ToString;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "orderId",
        "clientId",
        "cusip",
        "amount",
        "isPend"
})
@ToString
public class PendOrder {
    @JsonProperty("orderId")
    String orderId;
    @JsonProperty("clientId")
    String clientId;
    @JsonProperty("cusip")
    String cusip;
    @JsonProperty("amount")
    Long amount;
    @JsonProperty("isPend")
    Boolean isPend;

    public PendOrder() {
        super();
    }

    public PendOrder(String orderId, String clientId, String cusip, Long amount, Boolean isPend) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.cusip = cusip;
        this.amount = amount;
        this.isPend = isPend;
    }

    @JsonProperty("orderId")
    public String getOrderId() {
        return orderId;
    }
    @JsonProperty("orderId")
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    @JsonProperty("clientId")
    public String getClientId() {
        return clientId;
    }
    @JsonProperty("clientId")
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    @JsonProperty("cusip")
    public String getCusip() {
        return cusip;
    }
    @JsonProperty("cusip")
    public void setCusip(String cusip) {
        this.cusip = cusip;
    }
    @JsonProperty("amount")
    public Long getAmount() {
        return amount;
    }
    @JsonProperty("amount")
    public void setAmount(Long amount) {
        this.amount = amount;
    }
    @JsonProperty("isPend")
    public Boolean getPend() {
        return isPend;
    }
    @JsonProperty("isPend")
    public void setPend(Boolean pend) {
        isPend = pend;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PendOrder pendOrder = (PendOrder) o;
        return Objects.equals(orderId, pendOrder.orderId) && Objects.equals(clientId, pendOrder.clientId) && Objects.equals(cusip, pendOrder.cusip) && Objects.equals(amount, pendOrder.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, clientId, cusip, amount);
    }
}
