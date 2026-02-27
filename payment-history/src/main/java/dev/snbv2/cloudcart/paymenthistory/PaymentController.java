package dev.snbv2.cloudcart.paymenthistory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller that exposes endpoints for querying payment history records.
 */
@CommonsLog
@RestController
public class PaymentController {

    private final PaymentRepository paymentRepository;

    /**
     * Constructs a PaymentController with the given repository.
     *
     * @param paymentRepository the payment repository
     */
    public PaymentController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * Retrieves all payment records from the repository.
     *
     * @return a list of all stored payments
     */
    @GetMapping("/payments")
    public List<Payment> getAllPayments() {

        Iterable<Payment> paymentIterator = paymentRepository.findAll();
        List<Payment> payments = new ArrayList<>();

        for (Payment p : paymentIterator) {
            payments.add(p);
        }

        log.debug(String.format("All payments found = [%s]", payments));
        return payments;
    }

    /**
     * Retrieves a single payment record by its identifier.
     *
     * @param id the unique identifier of the payment to retrieve
     * @return the matching payment
     * @throws ResponseStatusException with 404 status if not found
     */
    @GetMapping("/payments/{id}")
    public Payment getPaymentById(@PathVariable("id") Integer id) {

        Optional<Payment> payment =  paymentRepository.findById(id);

        if (payment.isPresent()) {
            log.debug(String.format("Found payment [%s].", payment.get()));
            return payment.get();
        } else {
            log.debug("Payment not found.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found for id " + id);
        }
    }
}
