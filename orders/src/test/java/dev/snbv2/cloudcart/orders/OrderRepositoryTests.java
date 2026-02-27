package dev.snbv2.cloudcart.orders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DataJpaTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderRepositoryTests {

    @Autowired
    OrderRepository orderRepository;

    @Test
    void testFindAll() throws Exception {
        List<Order> orders = new ArrayList<>();
        for (Order order : orderRepository.findAll()) {
            orders.add(order);
        }
        Assertions.assertTrue(orders.isEmpty());
    }

    @Test
    void testSave() throws Exception {
        Order order = new Order();
        order.setFirstName("First name");
        order.setLastName("Last name");
        order.setAddress("Address");
        order.setAddress2("Address 2");
        order.setCity("City");
        order.setState("State");
        order.setZipCode("Zip code");
        order.setAmount(BigDecimal.valueOf(123.45));

        order = orderRepository.save(order);
        Assertions.assertNotNull(order.getId());
    }

    @Test
    void testFindById() throws Exception {
        Order order = new Order();
        order.setFirstName("Jane");
        order.setLastName("Doe");
        order.setAddress("123 Main St");
        order.setCity("Springfield");
        order.setState("IL");
        order.setZipCode("62701");
        order.setAmount(BigDecimal.valueOf(49.99));
        order.setCatalogItemId(100);

        order = orderRepository.save(order);
        Integer savedId = order.getId();

        Optional<Order> found = orderRepository.findById(savedId);
        Assertions.assertTrue(found.isPresent());
        Assertions.assertEquals("Jane", found.get().getFirstName());
        Assertions.assertEquals("Doe", found.get().getLastName());
        Assertions.assertEquals(BigDecimal.valueOf(49.99), found.get().getAmount());
        Assertions.assertEquals(100, found.get().getCatalogItemId());
    }

    @Test
    void testFindByIdNotFound() throws Exception {
        Optional<Order> found = orderRepository.findById(99999);
        Assertions.assertFalse(found.isPresent());
    }
}
