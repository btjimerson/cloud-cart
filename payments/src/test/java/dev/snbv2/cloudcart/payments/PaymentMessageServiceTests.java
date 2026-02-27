package dev.snbv2.cloudcart.payments;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;

public class PaymentMessageServiceTests {

    PaymentMessageService paymentMessageService;
    AmqpTemplate amqpTemplate;

    @BeforeEach
    void setUp() {
        amqpTemplate = Mockito.mock(AmqpTemplate.class);
        paymentMessageService = new PaymentMessageService(amqpTemplate);
    }

    @Test
    void testSendMessage() {
        Payment payment = new Payment("4242424242424242", "123", 12, 25, BigDecimal.valueOf(50.00), "usd", "Test payment");

        paymentMessageService.sendMessage(payment);

        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        Mockito.verify(amqpTemplate).convertAndSend(routingKeyCaptor.capture(), messageCaptor.capture());

        Assertions.assertEquals("payments", routingKeyCaptor.getValue());
        Assertions.assertNotNull(messageCaptor.getValue());
    }

    @Test
    void testSendMessageContainsPaymentData() {
        Payment payment = new Payment("4242424242424242", "123", 12, 25, BigDecimal.valueOf(99.99), "usd", "Order payment");

        paymentMessageService.sendMessage(payment);

        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);
        Mockito.verify(amqpTemplate).convertAndSend(routingKeyCaptor.capture(), messageCaptor.capture());

        String messageJson = (String) messageCaptor.getValue();
        Assertions.assertTrue(messageJson.contains("4242424242424242"));
        Assertions.assertTrue(messageJson.contains("99.99"));
    }
}
