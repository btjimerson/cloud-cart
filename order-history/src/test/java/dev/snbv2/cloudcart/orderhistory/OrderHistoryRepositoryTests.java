package dev.snbv2.cloudcart.orderhistory;

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
public class OrderHistoryRepositoryTests {

    @Autowired
    OrderHistoryRepository orderHistoryRepository;

    @Test
    void testSave() {
        OrderHistory history = new OrderHistory();
        history.setCorrelationId("corr-123");
        history.setPurchaseAmount(BigDecimal.valueOf(55.00));
        history.setOrderStatus("PLACED");

        history = orderHistoryRepository.save(history);

        Optional<OrderHistory> found = orderHistoryRepository.findById(history.getId());
        Assertions.assertTrue(found.isPresent());
        Assertions.assertNotNull(found.get().getId());
        Assertions.assertEquals("corr-123", found.get().getCorrelationId());
    }

    @Test
    void testFindAll() {
        List<OrderHistory> records = new ArrayList<>();
        orderHistoryRepository.findAll().forEach(records::add);
        Assertions.assertEquals(0, records.size());

        OrderHistory history = new OrderHistory();
        history.setCorrelationId("corr-456");
        history.setPurchaseAmount(BigDecimal.valueOf(25.00));
        history.setOrderStatus("PLACED");
        orderHistoryRepository.save(history);

        records.clear();
        orderHistoryRepository.findAll().forEach(records::add);
        Assertions.assertEquals(1, records.size());
    }

    @Test
    void testFindByCorrelationId() {
        OrderHistory history = new OrderHistory();
        history.setCorrelationId("corr-789");
        history.setPurchaseAmount(BigDecimal.valueOf(100.00));
        history.setOrderStatus("PLACED");
        orderHistoryRepository.save(history);

        Optional<OrderHistory> found = orderHistoryRepository.findByCorrelationId("corr-789");
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("corr-789", found.get().getCorrelationId());
    }

    @Test
    void testFindByIdNotFound() {
        Optional<OrderHistory> found = orderHistoryRepository.findById(99999);
        Assertions.assertFalse(found.isPresent());
    }
}
