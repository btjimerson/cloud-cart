package dev.snbv2.cloudcart.paymenthistory;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class PaymentControllerTests {

    @Autowired
    WebApplicationContext context;

    @Autowired
    PaymentRepository paymentRepository;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testGetAllPayments() throws Exception {
        mvc.perform(get("/payments"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
    }

    @Test
    void testGetPaymentById() throws Exception {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("55.00"));
        payment.setCardNumber("4242424242424242");
        payment.setCurrency("usd");
        payment.setCvc("123");
        payment.setDescription("Test payment");
        payment.setExpirationMonth(12);
        payment.setExpirationYear(25);
        payment = paymentRepository.save(payment);

        mvc.perform(get("/payments/" + payment.getId()))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(payment.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(55.00))
            .andExpect(MockMvcResultMatchers.jsonPath("$.currency").value("usd"));
    }

    @Test
    void testGetPaymentByIdNotFound() throws Exception {
        mvc.perform(get("/payments/99999"))
            .andExpect(status().isNotFound());
    }
}
