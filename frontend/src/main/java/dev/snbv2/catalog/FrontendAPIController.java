package dev.snbv2.catalog;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class FrontendAPIController {

    private static final Log LOG = LogFactory.getLog(FrontendAPIController.class);

    @Value("${endpoint.catalog}")
    private String catalogEndpoint;

    @Value("${endpoint.orders}")
    private String ordersEndpoint;

    @Value("${endpoint.payments}")
    private String paymentsEndpoint;

    @Value("${endpoint.payment-history}")
    private String paymentHistoryEndpoint;

    @Value("${app.version:sup}")
    private String appVersion;

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/catalog/{id}")
    public CatalogItem getCatalogItem(@PathVariable("id") Long id) {

        CatalogItem item = restTemplate.getForObject(
                catalogEndpoint + "/catalog/{id}", CatalogItem.class, id);

        LOG.debug(String.format("Catalog item retrieved = [%s]", item));

        return item;
    }

    @GetMapping("/catalog")
    public CatalogItem[] getAllCatalogItems() {

        CatalogItem[] catalogItems = restTemplate.getForEntity(
                catalogEndpoint + "/catalog", CatalogItem[].class).getBody();

        return catalogItems;
    }

    @PostMapping("/order")
    public OrderSummary placeOrder(@RequestBody Order order) {

        LOG.debug(String.format("Order being placed = [%s]", order));

        order.getPayment().setCurrency("usd");
        order.getPayment().setDescription(String.format("Order placed on %s", new Date()));

        String result = restTemplate.postForObject(
                paymentsEndpoint + "/payment", order.getPayment(), String.class);

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

        restTemplate.postForObject(ordersEndpoint + "/order", orderSummary, OrderSummary.class);

        return orderSummary;
    }

    @GetMapping("/payments")
    public @ResponseBody Payment[] getAllPayments() {

        Payment[] payments = restTemplate.getForObject(
                paymentHistoryEndpoint + "/payments", Payment[].class);

        if (payments != null) {
            LOG.debug(String.format("All payments found = [%s]", payments.toString()));
        }
        return payments;
    }

    @GetMapping("/payments/{id}")
    public @ResponseBody Payment getPaymentById(@PathVariable(name = "id") Long id) {

        Payment payment = restTemplate.getForObject(
                paymentHistoryEndpoint + "/payments/{id}", Payment.class, id);

        LOG.debug(String.format("Found payment [%s].", payment));
        return payment;
    }

    @GetMapping("/version")
    public String getAppVersion() {
        String returnVersion = String.format("{\"version\": \"%s\"}", appVersion);
        LOG.debug(String.format("App version = [%s]", returnVersion));
        return returnVersion;
    }

}
