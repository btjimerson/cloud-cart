package dev.snbv2.cloudcart.frontend;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpSession;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestClient;

@CommonsLog
@Controller
public class FrontendController {

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

    public FrontendController(RestClient restClient) {
        this.restClient = restClient;
    }

    @ModelAttribute
    public void populateAppVersion(Model model) {
        log.debug(String.format("Setting app version attribute to [%s]", appVersion));
        model.addAttribute("appVersion", appVersion);
    }

    @GetMapping({ "/", "/index" })
    public String index() {
        return "index";
    }

    @GetMapping("/item/{id}")
    public String getCatalogItem(@PathVariable("id") Long id, Model model) {

        CatalogItem item = restClient.get()
                .uri(catalogEndpoint + "/catalog/{id}", id)
                .retrieve()
                .body(CatalogItem.class);

        log.debug(String.format("Catalog item retrieved = [%s]", item));

        model.addAttribute("item", item);
        return "itemDetails";
    }

    @GetMapping("/catalog")
    public String getAllCatalogItems(Model model) {

        CatalogItem[] catalogItems = restClient.get()
                .uri(catalogEndpoint + "/catalog")
                .retrieve()
                .body(CatalogItem[].class);

        model.addAttribute("catalogItems", catalogItems);
        return "catalog";
    }

    @GetMapping("/cart/{id}")
    public String addToCart(@PathVariable("id") Long id, Model model, HttpSession session) {

        CatalogItem item = restClient.get()
                .uri(catalogEndpoint + "/catalog/{id}", id)
                .retrieve()
                .body(CatalogItem.class);

        log.debug(String.format("Catalog item retrieved = [%s]", item));

        this.getCart(session).add(item);

        CatalogItem[] catalogItems = restClient.get()
                .uri(catalogEndpoint + "/catalog")
                .retrieve()
                .body(CatalogItem[].class);

        model.addAttribute("catalogItems", catalogItems);
        if (item != null) {
            model.addAttribute("msg", item.getName() + " added to cart.");
        }
        return "catalog";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {

        Order order = new Order();
        order.setCatalogItems(this.getCart(session));
        order.setOrderTotal(this.getCartTotal(session));
        log.debug(String.format("Order = [%s]", order));

        model.addAttribute("order", order);
        return "checkout";
    }

    @PostMapping("/order")
    public String placeOrder(Order order, Model model, HttpSession session) {

        log.debug(String.format("Order being placed = [%s]", order));

        String correlationId = UUID.randomUUID().toString();

        order.getPayment().setCurrency("usd");
        order.getPayment().setAmount(this.getCartTotal(session));
        order.getPayment().setDescription(String.format("Order placed on %s", Instant.now()));
        order.getPayment().setCorrelationId(correlationId);

        String result = restClient.post()
                .uri(paymentsEndpoint + "/payment")
                .body(order.getPayment())
                .retrieve()
                .body(String.class);

        OrderSummary orderSummary = new OrderSummary();
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

        OrderSummary savedOrder = restClient.post()
                .uri(ordersEndpoint + "/order")
                .body(orderSummary)
                .retrieve()
                .body(OrderSummary.class);

        log.debug(String.format("Result of order = [%s]", savedOrder));

        String formattedDate = Instant.now()
                .atZone(java.time.ZoneId.systemDefault())
                .format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a"));
        Integer orderId = savedOrder != null ? savedOrder.getId() : null;

        if ("succeeded".equalsIgnoreCase(result)) {
            model.addAttribute("result", String.format(
                    "Payment for order %s was successful on %s.", orderId, formattedDate));
            session.removeAttribute("cart");
        } else {
            model.addAttribute("result", String.format(
                    "Payment for order %s was %s on %s.", orderId, result, formattedDate));
        }

        model.addAttribute("order", order);
        return "orderResult";
    }

    @GetMapping("/order-history")
    public String getAllOrderHistory(Model model) {

        OrderHistoryRecord[] records = restClient.get()
                .uri(orderHistoryEndpoint + "/order-history")
                .retrieve()
                .body(OrderHistoryRecord[].class);
        if (records != null) {
            log.debug(String.format("All order history found = [%s]", Arrays.toString(records)));
        }
        model.addAttribute("records", records);
        return "orderHistory";
    }

    @GetMapping("/order-history/{id}")
    public String getOrderHistoryById(@PathVariable("id") Long id, Model model) {

        OrderHistoryRecord record = restClient.get()
                .uri(orderHistoryEndpoint + "/order-history/{id}", id)
                .retrieve()
                .body(OrderHistoryRecord.class);

        log.debug(String.format("Found order history [%s].", record));
        model.addAttribute("record", record);
        return "orderHistoryDetails";
    }

    private List<CatalogItem> getCart(HttpSession session) {
        if (session.getAttribute("cart") == null) {
            session.setAttribute("cart", new ArrayList<CatalogItem>());
        }

        @SuppressWarnings("unchecked")
        List<CatalogItem> cart = (List<CatalogItem>) session.getAttribute("cart");

        return cart;
    }

    private BigDecimal getCartTotal(HttpSession session) {

        List<CatalogItem> cart = this.getCart(session);
        BigDecimal orderTotal = BigDecimal.ZERO;
        for (CatalogItem item : cart) {
            if (item != null && item.getAmount() != null) {
                orderTotal = orderTotal.add(item.getAmount());
            }
        }

        return orderTotal;
    }
}
