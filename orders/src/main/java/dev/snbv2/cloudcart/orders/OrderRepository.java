package dev.snbv2.cloudcart.orders;

import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data repository interface for performing CRUD operations on {@link Order} entities.
 */
public interface OrderRepository extends CrudRepository<Order, Integer> {

}
