package com.sarkar.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Setter;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "client",
        "order"
})
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClientOrder {
    @JsonProperty("client")
    Client client;
    @JsonProperty("order")
    PendOrder order;


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ClientOrder that = (ClientOrder) o;
        return Objects.equals(client, that.client) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(client);
        result = 31 * result + Objects.hashCode(order);
        return result;
    }
}
