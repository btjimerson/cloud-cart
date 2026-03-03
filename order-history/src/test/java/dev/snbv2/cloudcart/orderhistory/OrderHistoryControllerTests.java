package dev.snbv2.cloudcart.orderhistory;

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
public class OrderHistoryControllerTests {

    @Autowired
    WebApplicationContext context;

    @Autowired
    OrderHistoryRepository orderHistoryRepository;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testGetAllOrderHistory() throws Exception {
        mvc.perform(get("/order-history"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
    }

    @Test
    void testGetOrderHistoryById() throws Exception {
        OrderHistory history = new OrderHistory();
        history.setCorrelationId("corr-test");
        history.setPurchaseAmount(new BigDecimal("55.00"));
        history.setOrderStatus("PLACED");
        history.setPaymentStatus("COMPLETED");
        history = orderHistoryRepository.save(history);

        mvc.perform(get("/order-history/" + history.getId()))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(history.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.correlationId").value("corr-test"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.purchaseAmount").value(55.00));
    }

    @Test
    void testGetOrderHistoryByIdNotFound() throws Exception {
        mvc.perform(get("/order-history/99999"))
            .andExpect(status().isNotFound());
    }
}
