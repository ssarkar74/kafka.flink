package com.sarkar.kafka.stream.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.ToString;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "clientOrders"
})
@ToString
public class ClientOrders {
    @JsonProperty("clientOrders")
    Set<ClientOrder> clientOrderSet;

    public ClientOrders() {
        this.clientOrderSet = new HashSet<>();
    }
    @JsonProperty("clientOrders")
    public Set<ClientOrder> getClientOrderSet() {
        return clientOrderSet;
    }
    @JsonProperty("clientOrders")
    public void setClientOrderSet(Set<ClientOrder> clientOrderSet) {
        this.clientOrderSet = clientOrderSet;
    }
    public ClientOrders withClientOrder(ClientOrder clientOrder){
        if (this.clientOrderSet.contains(clientOrder)) {

            this.clientOrderSet.remove(clientOrder);
        }
        this.clientOrderSet.add(clientOrder);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClientOrders that = (ClientOrders) o;
        return Objects.equals(clientOrderSet, that.clientOrderSet);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(clientOrderSet);
    }

    public ClientOrders withClientOrders(Set<ClientOrder> clientOrders) {
        this.clientOrderSet = clientOrders;
        return this;
    }
}
