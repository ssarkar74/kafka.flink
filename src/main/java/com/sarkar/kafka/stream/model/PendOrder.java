package com.sarkar.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Setter;

import java.io.Serializable;
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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PendOrder implements Serializable {
    private static final long serialVersionUID=1234L;
    @JsonProperty("orderId")
    String orderId;
    @JsonProperty("clientId")
    String clientId;
    @JsonProperty("cusip")
    String cusip;
    @JsonProperty("amount")
    Long amount;
    @JsonProperty("orderTime")
    long orderTime;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        PendOrder pendOrder = (PendOrder) o;
        return orderTime == pendOrder.orderTime && Objects.equals(orderId, pendOrder.orderId) && Objects.equals(clientId, pendOrder.clientId) && Objects.equals(cusip, pendOrder.cusip) && Objects.equals(amount, pendOrder.amount);
    }

   /* @Override
    public int hashCode() {
        int result = Objects.hashCode(orderId);
        result = 31 * result + Objects.hashCode(clientId);
        result = 31 * result + Objects.hashCode(cusip);
        result = 31 * result + Objects.hashCode(amount);
        result = 31 * result + Long.hashCode(orderTime);
        return result;
    }*/
}
