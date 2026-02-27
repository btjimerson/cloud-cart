package dev.snbv2.cloudcart.payments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

/**
 * Service that publishes payment information to a RabbitMQ message queue.
 */
@CommonsLog
@Service
public class PaymentMessageService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final AmqpTemplate amqpTemplate;

    /**
     * Constructs a PaymentMessageService with the required AMQP template.
     *
     * @param amqpTemplate the AMQP template for sending messages
     */
    public PaymentMessageService(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    /**
     * Serializes the payment to JSON and sends it to the payments message queue.
     *
     * @param payment the payment to serialize and send
     */
    public void sendMessage(Payment payment) {

        String paymentJson = null;
        try {
            paymentJson = OBJECT_MAPPER.writeValueAsString(payment);
        } catch (JsonProcessingException e) {
            log.error("Error converting Payment to JSON.", e);
            throw new RuntimeException(e);
        }

        log.debug(String.format("Sending payment %s", paymentJson));
        amqpTemplate.convertAndSend("payments", paymentJson);

    }

}
