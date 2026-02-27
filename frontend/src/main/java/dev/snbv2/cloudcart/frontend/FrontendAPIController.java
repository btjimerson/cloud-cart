package dev.snbv2.cloudcart.frontend;

import java.time.Instant;
import java.util.Arrays;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

/**
 * REST API controller that exposes JSON endpoints for catalog browsing,
 * order placement, payment retrieval, and application version information.
 */
@CommonsLog
@RestController
@RequestMapping("/api")
public class FrontendAPIController {

    @Value("${endpoint.catalog}")
    private String catalogEndpoint;

    @Value("${endpoint.orders}")
    private String ordersEndpoint;

    @Value("${endpoint.payments}")
    private String paymentsEndpoint;

    @Value("${endpoint.payment-history}")
    private String paymentHistoryEndpoint;

    @Value("${APP_VERSION:}")
    private String appVersion;

    private final RestClient restClient;

    /**
     * Constructs a FrontendAPIController with the given RestClient.
     *
     * @param restClient the RestClient used to communicate with backend services
     */
    public FrontendAPIController(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Retrieves a single catalog item by its identifier.
     *
     * @param id the catalog item identifier
     * @return the catalog item
     */
    @GetMapping("/catalog/{id}")
    public CatalogItem getCatalogItem(@PathVariable("id") Long id) {

        CatalogItem item = restClient.get()
                .uri(catalogEndpoint + "/catalog/{id}", id)
                .retrieve()
                .body(CatalogItem.class);

        log.debug(String.format("Catalog item retrieved = [%s]", item));

        return item;
    }

    /**
     * Retrieves all catalog items from the catalog service.
     *
     * @return an array of all catalog items
     */
    @GetMapping("/catalog")
    public CatalogItem[] getAllCatalogItems() {

        CatalogItem[] catalogItems = restClient.get()
                .uri(catalogEndpoint + "/catalog")
                .retrieve()
                .body(CatalogItem[].class);

        return catalogItems;
    }

    /**
     * Places an order by processing the payment and persisting the order summary.
     *
     * @param order the order containing catalog items, billing address, and payment details
     * @return the order summary including the payment result
     */
    @PostMapping("/order")
    public OrderSummary placeOrder(@RequestBody Order order) {

        log.debug(String.format("Order being placed = [%s]", order));

        order.getPayment().setCurrency("usd");
        order.getPayment().setDescription(String.format("Order placed on %s", Instant.now()));

        String result = restClient.post()
                .uri(paymentsEndpoint + "/payment")
                .body(order.getPayment())
                .retrieve()
                .body(String.class);

        OrderSummary orderSummary = new OrderSummary();
        orderSummary.setResult(result);
        orderSummary.setAddress(order.getBillingAddress().getAddress());
        orderSummary.setAddress2(order.getBillingAddress().getAddress2());
        orderSummary.setAmount(order.getPayment().getAmount());
        orderSummary.setCatalogItems(order.getCatalogItems());
        orderSummary.setCity(order.getBillingAddress().getCity());
        orderSummary.setFirstName(order.getBillingAddress().getFirstName());
        orderSummary.setLastName(order.getBillingAddress().getLastName());
        orderSummary.setState(order.getBillingAddress().getState());
        orderSummary.setZipCode(order.getBillingAddress().getZipCode());

        restClient.post()
                .uri(ordersEndpoint + "/order")
                .body(orderSummary)
                .retrieve()
                .body(OrderSummary.class);

        return orderSummary;
    }

    /**
     * Retrieves all payments from the payment history service.
     *
     * @return an array of all payments
     */
    @GetMapping("/payments")
    public Payment[] getAllPayments() {

        Payment[] payments = restClient.get()
                .uri(paymentHistoryEndpoint + "/payments")
                .retrieve()
                .body(Payment[].class);

        if (payments != null) {
            log.debug(String.format("All payments found = [%s]", Arrays.toString(payments)));
        }
        return payments;
    }

    /**
     * Retrieves a single payment by its identifier.
     *
     * @param id the payment identifier
     * @return the payment
     */
    @GetMapping("/payments/{id}")
    public Payment getPaymentById(@PathVariable(name = "id") Long id) {

        Payment payment = restClient.get()
                .uri(paymentHistoryEndpoint + "/payments/{id}", id)
                .retrieve()
                .body(Payment.class);

        log.debug(String.format("Found payment [%s].", payment));
        return payment;
    }

    /**
     * Returns the application version as a JSON-formatted string.
     *
     * @return a JSON string containing the application version
     */
    @GetMapping("/version")
    public String getAppVersion() {
        String returnVersion = String.format("{\"version\": \"%s\"}", appVersion);
        log.debug(String.format("App version = [%s]", returnVersion));
        return returnVersion;
    }

}
