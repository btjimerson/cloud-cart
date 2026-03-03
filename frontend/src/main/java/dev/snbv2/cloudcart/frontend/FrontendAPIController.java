package dev.snbv2.cloudcart.frontend;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

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

    @Value("${endpoint.order-history}")
    private String orderHistoryEndpoint;

    @Value("${APP_VERSION:}")
    private String appVersion;

    private final RestClient restClient;

    public FrontendAPIController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/catalog/{id}")
    public CatalogItem getCatalogItem(@PathVariable("id") Long id) {

        CatalogItem item = restClient.get()
                .uri(catalogEndpoint + "/catalog/{id}", id)
                .retrieve()
                .body(CatalogItem.class);

        log.debug(String.format("Catalog item retrieved = [%s]", item));

        return item;
    }

    @GetMapping("/catalog")
    public CatalogItem[] getAllCatalogItems() {

        CatalogItem[] catalogItems = restClient.get()
                .uri(catalogEndpoint + "/catalog")
                .retrieve()
                .body(CatalogItem[].class);

        return catalogItems;
    }

    @PostMapping("/order")
    public OrderSummary placeOrder(@RequestBody Order order) {

        log.debug(String.format("Order being placed = [%s]", order));

        String correlationId = UUID.randomUUID().toString();

        order.getPayment().setCurrency("usd");
        order.getPayment().setDescription(String.format("Order placed on %s", Instant.now()));
        order.getPayment().setCorrelationId(correlationId);

        String result = restClient.post()
                .uri(paymentsEndpoint + "/payment")
                .body(order.getPayment())
                .retrieve()
                .body(String.class);

        OrderSummary orderSummary = new OrderSummary();
        orderSummary.setResult(result);
        orderSummary.setCorrelationId(correlationId);
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

    @GetMapping("/order-history")
    public OrderHistoryRecord[] getAllOrderHistory() {

        OrderHistoryRecord[] records = restClient.get()
                .uri(orderHistoryEndpoint + "/order-history")
                .retrieve()
                .body(OrderHistoryRecord[].class);

        if (records != null) {
            log.debug(String.format("All order history found = [%s]", Arrays.toString(records)));
        }
        return records;
    }

    @GetMapping("/order-history/{id}")
    public OrderHistoryRecord getOrderHistoryById(@PathVariable(name = "id") Long id) {

        OrderHistoryRecord record = restClient.get()
                .uri(orderHistoryEndpoint + "/order-history/{id}", id)
                .retrieve()
                .body(OrderHistoryRecord.class);

        log.debug(String.format("Found order history [%s].", record));
        return record;
    }

    @GetMapping("/version")
    public String getAppVersion() {
        String returnVersion = String.format("{\"version\": \"%s\"}", appVersion);
        log.debug(String.format("App version = [%s]", returnVersion));
        return returnVersion;
    }

}
