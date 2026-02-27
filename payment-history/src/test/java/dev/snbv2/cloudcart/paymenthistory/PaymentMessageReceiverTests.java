package dev.snbv2.cloudcart.paymenthistory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PaymentMessageReceiverTests {

    @Autowired
    PaymentMessageReceiver paymentMessageReceiver;

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void testReceiveMessage() throws Exception {
        String message = "{" +
            "\"cardNumber\": \"4242424242424242\"," +
            "\"cvc\": \"123\"," +
            "\"expirationMonth\": 12," +
            "\"expirationYear\": 25," +
            "\"amount\": 75.50," +
            "\"currency\": \"usd\"," +
            "\"description\": \"Test payment\"" +
            "}";

        paymentMessageReceiver.receiveMessage(message);

        List<Payment> payments = new ArrayList<>();
        paymentRepository.findAll().forEach(payments::add);
        Assertions.assertEquals(1, payments.size());

        Payment saved = payments.get(0);
        Assertions.assertEquals("4242424242424242", saved.getCardNumber());
        Assertions.assertEquals(0, BigDecimal.valueOf(75.50).compareTo(saved.getAmount()));
        Assertions.assertEquals("usd", saved.getCurrency());
    }

    @Test
    void testReceiveInvalidMessage() {
        Assertions.assertThrows(Exception.class, () -> {
            paymentMessageReceiver.receiveMessage("invalid json");
        });
    }
}
