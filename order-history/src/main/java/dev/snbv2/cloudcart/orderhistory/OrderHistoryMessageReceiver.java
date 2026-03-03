package dev.snbv2.cloudcart.orderhistory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Component;

@CommonsLog
@Component
public class OrderHistoryMessageReceiver {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final OrderHistoryRepository orderHistoryRepository;

    public OrderHistoryMessageReceiver(OrderHistoryRepository orderHistoryRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
    }

    public void receiveOrderEvent(String message) {

        log.debug(String.format("Order event received = [%s]", message));

        try {
            var node = OBJECT_MAPPER.readTree(message);
            String correlationId = node.has("correlationId") ? node.get("correlationId").asText() : null;

            OrderHistory history = orderHistoryRepository.findByCorrelationId(correlationId)
                    .orElse(new OrderHistory());

            history.setCorrelationId(correlationId);

            if (node.has("purchaseDate")) {
                history.setPurchaseDate(node.get("purchaseDate").asText());
            }
            if (node.has("purchaseAmount")) {
                history.setPurchaseAmount(node.get("purchaseAmount").decimalValue());
            }
            if (node.has("numberOfItems")) {
                history.setNumberOfItems(node.get("numberOfItems").asInt());
            }
            if (node.has("orderStatus")) {
                history.setOrderStatus(node.get("orderStatus").asText());
            }

            orderHistoryRepository.save(history);
            log.debug(String.format("Order history [%s] saved from order event.", history));

        } catch (JsonProcessingException e) {
            log.error("Error processing order event message.", e);
            throw new RuntimeException(e);
        }
    }

    public void receivePaymentEvent(String message) {

        log.debug(String.format("Payment event received = [%s]", message));

        try {
            var node = OBJECT_MAPPER.readTree(message);
            String correlationId = node.has("correlationId") ? node.get("correlationId").asText() : null;

            OrderHistory history = orderHistoryRepository.findByCorrelationId(correlationId)
                    .orElse(new OrderHistory());

            history.setCorrelationId(correlationId);

            if (node.has("paymentStatus")) {
                history.setPaymentStatus(node.get("paymentStatus").asText());
            }

            orderHistoryRepository.save(history);
            log.debug(String.format("Order history [%s] saved from payment event.", history));

        } catch (JsonProcessingException e) {
            log.error("Error processing payment event message.", e);
            throw new RuntimeException(e);
        }
    }
}
