package dev.snbv2.cloudcart.payments;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.FanoutExchange;

public class PaymentMessageServiceTests {

    PaymentMessageService paymentMessageService;
    AmqpTemplate amqpTemplate;
    FanoutExchange fanoutExchange;

    @BeforeEach
    void setUp() {
        amqpTemplate = Mockito.mock(AmqpTemplate.class);
        fanoutExchange = new FanoutExchange("payments");
        paymentMessageService = new PaymentMessageService(amqpTemplate, fanoutExchange);
    }

    @Test
    void testSendMessage() {
        Payment payment = new Payment("4242424242424242", "123", 12, 25, BigDecimal.valueOf(50.00), "usd", "Test payment", "corr-123");

        paymentMessageService.sendMessage(payment);

        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        Mockito.verify(amqpTemplate).convertAndSend(exchangeCaptor.capture(), routingKeyCaptor.capture(), messageCaptor.capture());

        Assertions.assertEquals("payments", exchangeCaptor.getValue());
        Assertions.assertEquals("", routingKeyCaptor.getValue());
        Assertions.assertNotNull(messageCaptor.getValue());
    }

    @Test
    void testSendMessageContainsEventData() {
        Payment payment = new Payment("4242424242424242", "123", 12, 25, BigDecimal.valueOf(99.99), "usd", "Order payment", "corr-456");

        paymentMessageService.sendMessage(payment);

        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        Mockito.verify(amqpTemplate).convertAndSend(exchangeCaptor.capture(), routingKeyCaptor.capture(), messageCaptor.capture());

        String messageJson = (String) messageCaptor.getValue();
        Assertions.assertTrue(messageJson.contains("corr-456"));
        Assertions.assertTrue(messageJson.contains("COMPLETED"));
    }
}
