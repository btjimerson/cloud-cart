package dev.snbv2.cloudcart.paymenthistory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Component;

/**
 * Listener component that receives payment messages from the RabbitMQ queue
 * and persists them to the database.
 */
@CommonsLog
@Component
public class PaymentMessageReceiver {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final PaymentRepository paymentRepository;

    /**
     * Constructs a PaymentMessageReceiver with the given repository.
     *
     * @param paymentRepository the payment repository
     */
    public PaymentMessageReceiver(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Receives a payment message from the RabbitMQ queue, deserializes it,
     * and saves it to the payment repository.
     *
     * @param message the JSON payment message
     */
    public void receiveMessage(String message) {

        log.debug(String.format("Message received = [%s]", message));
        Payment payment = null;

        try {
            payment = OBJECT_MAPPER.readValue(message, Payment.class);
        } catch (JsonProcessingException e) {
            log.error("Error marshalling message to Payment.", e);
            throw new RuntimeException(e);
        }

        paymentRepository.save(payment);
        log.debug(String.format("Payment [%s] successfully saved.", payment));
    }
}
