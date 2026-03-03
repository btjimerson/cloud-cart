package dev.snbv2.cloudcart.orderhistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderHistoryMessageReceiverTests {

    @Autowired
    OrderHistoryMessageReceiver orderHistoryMessageReceiver;

    @Autowired
    OrderHistoryRepository orderHistoryRepository;

    @Test
    void testReceiveOrderEvent() {
        String message = "{" +
            "\"correlationId\": \"corr-100\"," +
            "\"purchaseDate\": \"2026-03-03T12:00:00Z\"," +
            "\"purchaseAmount\": 75.50," +
            "\"numberOfItems\": 2," +
            "\"orderStatus\": \"PLACED\"" +
            "}";

        orderHistoryMessageReceiver.receiveOrderEvent(message);

        List<OrderHistory> records = new ArrayList<>();
        orderHistoryRepository.findAll().forEach(records::add);
        Assertions.assertEquals(1, records.size());

        OrderHistory saved = records.get(0);
        Assertions.assertEquals("corr-100", saved.getCorrelationId());
        Assertions.assertEquals("PLACED", saved.getOrderStatus());
        Assertions.assertEquals(2, saved.getNumberOfItems());
    }

    @Test
    void testReceivePaymentEvent() {
        String message = "{" +
            "\"correlationId\": \"corr-200\"," +
            "\"paymentStatus\": \"COMPLETED\"" +
            "}";

        orderHistoryMessageReceiver.receivePaymentEvent(message);

        Optional<OrderHistory> found = orderHistoryRepository.findByCorrelationId("corr-200");
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("COMPLETED", found.get().getPaymentStatus());
    }

    @Test
    void testCorrelation() {
        String orderMessage = "{" +
            "\"correlationId\": \"corr-300\"," +
            "\"purchaseDate\": \"2026-03-03T12:00:00Z\"," +
            "\"purchaseAmount\": 100.00," +
            "\"numberOfItems\": 3," +
            "\"orderStatus\": \"PLACED\"" +
            "}";

        String paymentMessage = "{" +
            "\"correlationId\": \"corr-300\"," +
            "\"paymentStatus\": \"COMPLETED\"" +
            "}";

        orderHistoryMessageReceiver.receiveOrderEvent(orderMessage);
        orderHistoryMessageReceiver.receivePaymentEvent(paymentMessage);

        List<OrderHistory> records = new ArrayList<>();
        orderHistoryRepository.findAll().forEach(records::add);
        Assertions.assertEquals(1, records.size());

        OrderHistory correlated = records.get(0);
        Assertions.assertEquals("corr-300", correlated.getCorrelationId());
        Assertions.assertEquals("PLACED", correlated.getOrderStatus());
        Assertions.assertEquals("COMPLETED", correlated.getPaymentStatus());
        Assertions.assertEquals(3, correlated.getNumberOfItems());
    }

    @Test
    void testReceiveInvalidMessage() {
        Assertions.assertThrows(Exception.class, () -> {
            orderHistoryMessageReceiver.receiveOrderEvent("invalid json");
        });
    }
}
