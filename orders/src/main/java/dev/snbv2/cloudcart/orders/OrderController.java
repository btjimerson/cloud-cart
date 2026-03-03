package dev.snbv2.cloudcart.orders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@CommonsLog
@RestController
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderMessageService orderMessageService;

    public OrderController(OrderRepository orderRepository, OrderMessageService orderMessageService) {
        this.orderRepository = orderRepository;
        this.orderMessageService = orderMessageService;
    }

    /**
     * Retrieves all orders from the repository.
     *
     * @return a list of all orders
     */
    @GetMapping("/orders")
    public List<Order> getAllOrders() {

        Iterable<Order> ordersIterable = orderRepository.findAll();
        List<Order> orders = new ArrayList<>();

        for (Order o : ordersIterable) {
            orders.add(o);
        }

        log.debug(String.format("All orders = [%s]", orders));
        return orders;

    }

    /**
     * Saves a new order to the repository.
     *
     * @param order the order to save
     * @return the saved order with its generated ID
     */
    @PostMapping("/order")
    public Order saveOrder(@RequestBody Order order) {

        order = orderRepository.save(order);
        log.debug(String.format("Saved order [%s]", order));

        OrderPlacedEvent event = new OrderPlacedEvent(
                order.getCorrelationId(),
                Instant.now().toString(),
                order.getAmount(),
                1,
                "PLACED"
        );
        orderMessageService.sendMessage(event);

        return order;

    }

    /**
     * Retrieves a single order by its ID.
     *
     * @param id the ID of the order to retrieve
     * @return the order if found
     * @throws ResponseStatusException with 404 status if not found
     */
    @GetMapping("/order/{id}")
    public Order getOrder(@PathVariable("id") Integer id) {

        Optional<Order> order = orderRepository.findById(id);

        if (order.isPresent()) {
            log.debug(String.format("Order retrieved = [%s]", order.get()));
            return order.get();
        } else {
            log.info(String.format("No order found for id [%d]", id));
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found for id " + id);
        }

    }
}
