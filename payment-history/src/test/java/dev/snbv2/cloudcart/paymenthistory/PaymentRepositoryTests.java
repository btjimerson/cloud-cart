package dev.snbv2.cloudcart.paymenthistory;

import java.math.BigDecimal;
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
public class PaymentRepositoryTests {

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void testSave() {
        Payment payment = new Payment();
        payment.setAmount(BigDecimal.valueOf(55.00));
        payment.setCardNumber("5555555555554444");
        payment.setCurrency("usd");
        payment.setCvc("999");
        payment.setDescription("Test payment");
        payment.setExpirationMonth(12);
        payment.setExpirationYear(24);

        payment = paymentRepository.save(payment);

        Optional<Payment> payment2 = paymentRepository.findById(payment.getId());
        Assertions.assertTrue(payment2.isPresent());
        Assertions.assertNotNull(payment2.get().getId());
        Assertions.assertTrue(payment2.get().getCardNumber().equalsIgnoreCase("5555555555554444"));
    }

    @Test
    void testFindAll() {
        List<Payment> payments = new ArrayList<>();
        paymentRepository.findAll().forEach(payments::add);
        Assertions.assertEquals(0, payments.size());

        Payment payment = new Payment();
        payment.setAmount(BigDecimal.valueOf(25.00));
        payment.setCardNumber("4242424242424242");
        payment.setCurrency("usd");
        payment.setCvc("123");
        payment.setDescription("Test");
        payment.setExpirationMonth(6);
        payment.setExpirationYear(26);
        paymentRepository.save(payment);

        payments.clear();
        paymentRepository.findAll().forEach(payments::add);
        Assertions.assertEquals(1, payments.size());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<Payment> payment = paymentRepository.findById(99999);
        Assertions.assertFalse(payment.isPresent());
    }
}
