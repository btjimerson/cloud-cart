package dev.snbv2.cloudcart.paymenthistory;

import org.springframework.data.repository.CrudRepository;

/**
 * Spring Data repository interface for performing CRUD operations on {@link Payment} entities.
 */
public interface PaymentRepository extends CrudRepository<Payment, Integer> {

}
