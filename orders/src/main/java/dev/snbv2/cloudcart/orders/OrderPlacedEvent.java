package dev.snbv2.cloudcart.orders;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderPlacedEvent {

    private String correlationId;
    private String purchaseDate;
    private BigDecimal purchaseAmount;
    private Integer numberOfItems;
    private String orderStatus;

    public OrderPlacedEvent() {
    }

    public OrderPlacedEvent(String correlationId, String purchaseDate, BigDecimal purchaseAmount,
            Integer numberOfItems, String orderStatus) {
        this.correlationId = correlationId;
        this.purchaseDate = purchaseDate;
        this.purchaseAmount = purchaseAmount;
        this.numberOfItems = numberOfItems;
        this.orderStatus = orderStatus;
    }

}
