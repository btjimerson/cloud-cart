package dev.snbv2.cloudcart.payments;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes the payment processing endpoint.
 */
@CommonsLog
@RestController
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMessageService paymentMessageService;

    /**
     * Constructs a PaymentController with the required services.
     *
     * @param paymentService the payment processing service
     * @param paymentMessageService the payment messaging service
     */
    public PaymentController(PaymentService paymentService, PaymentMessageService paymentMessageService) {
        this.paymentService = paymentService;
        this.paymentMessageService = paymentMessageService;
    }

    /**
     * Processes a payment by delegating to the payment service and sending a message to the queue.
     *
     * @param payment the payment details submitted in the request body
     * @return the charge status from Stripe, or an error message if processing fails
     */
    @PostMapping("/payment")
    public String processPayment(@RequestBody Payment payment) {

        String result = null;
        try {
            result = paymentService.processPayment(payment);
            paymentMessageService.sendMessage(payment);
        } catch (Exception e) {
            log.error("Error processing payment", e);
            result = e.getMessage();
        }

        return result;
    }
}
