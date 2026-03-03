package dev.snbv2.cloudcart.orders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@CommonsLog
@Service
public class OrderMessageService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final AmqpTemplate amqpTemplate;
    private final FanoutExchange fanoutExchange;

    public OrderMessageService(AmqpTemplate amqpTemplate, FanoutExchange fanoutExchange) {
        this.amqpTemplate = amqpTemplate;
        this.fanoutExchange = fanoutExchange;
    }

    public void sendMessage(OrderPlacedEvent event) {

        String eventJson = null;
        try {
            eventJson = OBJECT_MAPPER.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Error converting OrderPlacedEvent to JSON.", e);
            throw new RuntimeException(e);
        }

        log.debug(String.format("Sending order event %s", eventJson));
        amqpTemplate.convertAndSend(fanoutExchange.getName(), "", eventJson);

    }

}
