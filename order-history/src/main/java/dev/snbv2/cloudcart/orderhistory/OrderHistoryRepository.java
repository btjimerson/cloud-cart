package dev.snbv2.cloudcart.orderhistory;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface OrderHistoryRepository extends CrudRepository<OrderHistory, Integer> {

    Optional<OrderHistory> findByCorrelationId(String correlationId);

}
