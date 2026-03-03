package dev.snbv2.cloudcart.orderhistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@CommonsLog
@RestController
public class OrderHistoryController {

    private final OrderHistoryRepository orderHistoryRepository;

    public OrderHistoryController(OrderHistoryRepository orderHistoryRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
    }

    @GetMapping("/order-history")
    public List<OrderHistory> getAllOrderHistory() {

        Iterable<OrderHistory> historyIterable = orderHistoryRepository.findAll();
        List<OrderHistory> history = new ArrayList<>();

        for (OrderHistory h : historyIterable) {
            history.add(h);
        }

        log.debug(String.format("All order history found = [%s]", history));
        return history;
    }

    @GetMapping("/order-history/{id}")
    public OrderHistory getOrderHistoryById(@PathVariable("id") Integer id) {

        Optional<OrderHistory> history = orderHistoryRepository.findById(id);

        if (history.isPresent()) {
            log.debug(String.format("Found order history [%s].", history.get()));
            return history.get();
        } else {
            log.debug("Order history not found.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order history not found for id " + id);
        }
    }
}
