package dev.snbv2.cloudcart.payments;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentProcessedEvent {

    private String correlationId;
    private String paymentStatus;

    public PaymentProcessedEvent() {
    }

    public PaymentProcessedEvent(String correlationId, String paymentStatus) {
        this.correlationId = correlationId;
        this.paymentStatus = paymentStatus;
    }

}
