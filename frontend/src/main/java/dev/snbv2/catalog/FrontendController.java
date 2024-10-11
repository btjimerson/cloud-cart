package dev.snbv2.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class FrontendController {

    private static final Log LOG = LogFactory.getLog(FrontendController.class);

    @Value("${endpoint.catalog}")
    private String catalogEndpoint;

    @Value("${endpoint.orders}")
    private String ordersEndpoint;

    @Value("${endpoint.payments}")
    private String paymentsEndpoint;

    @Value("${endpoint.payment-history}")
    private String paymentHistoryEndpoint;

    @Autowired
    RestTemplate restTemplate;

    @ModelAttribute
    public void populateAppVersion(Model model) {
        String appVersion = System.getenv("APP_VERSION");
        LOG.debug(String.format("Setting app version attribute to [%s]", appVersion));
        model.addAttribute("appVersion", appVersion == null ? "" : appVersion);
    }

    @GetMapping({ "/", "/index" })
    public String index() {
        return "index";
    }

    @GetMapping("/item/{id}")
    public String getCatalogItem(@PathVariable("id") Long id, Model model) {

        CatalogItem item = restTemplate.getForObject(
                catalogEndpoint + "/catalog/{id}", CatalogItem.class, id);

        LOG.debug(String.format("Catalog item retrieved = [%s]", item));

        model.addAttribute("item", item);
        return "itemDetails";
    }

    @GetMapping("/catalog")
    public String getAllCatalogItems(Model model) {

        CatalogItem[] catalogItems = restTemplate.getForEntity(
                catalogEndpoint + "/catalog", CatalogItem[].class).getBody();

        model.addAttribute("catalogItems", catalogItems);
        return "catalog";
    }

    @GetMapping("/cart/{id}")
    public String addToCart(@PathVariable("id") Long id, Model model, HttpSession session) {

        CatalogItem item = restTemplate.getForObject(
                catalogEndpoint + "/catalog/{id}", CatalogItem.class, id);

        LOG.debug(String.format("Catalog item retrieved = [%s]", item));

        this.getCart(session).add(item);

        CatalogItem[] catalogItems = restTemplate.getForEntity(
                catalogEndpoint + "/catalog", CatalogItem[].class).getBody();

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
        LOG.debug(String.format("Order = [%s]", order));

        model.addAttribute("order", order);
        return "checkout";
    }

    @PostMapping("/order")
    public String placeOrder(Order order, Model model, HttpSession session) {

        LOG.debug(String.format("Order being placed = [%s]", order));

        order.getPayment().setCurrency("usd");
        order.getPayment().setAmount(this.getCartTotal(session));
        order.getPayment().setDescription(String.format("Order placed on %s", new Date()));

        String result = restTemplate.postForObject(
                paymentsEndpoint + "/payment", order.getPayment(), String.class);

        OrderSummary orderSummary = new OrderSummary();
        orderSummary.setAddress(order.getBillingAddress().getAddress());
        orderSummary.setAddress2(order.getBillingAddress().getAddress2());
        orderSummary.setAmount(order.getPayment().getAmount());
        orderSummary.setCatalogItems(order.getCatalogItems());
        orderSummary.setCity(order.getBillingAddress().getCity());
        orderSummary.setFirstName(order.getBillingAddress().getFirstName());
        orderSummary.setLastName(order.getBillingAddress().getLastName());
        orderSummary.setState(order.getBillingAddress().getState());
        orderSummary.setZipCode(order.getBillingAddress().getZipCode());

        OrderSummary savedOrder = restTemplate.postForObject(
                ordersEndpoint + "/order", orderSummary, OrderSummary.class);

        LOG.debug(String.format("Result of order = [%s]", savedOrder));

        if ("succeeded".equalsIgnoreCase(result)) {
            model.addAttribute("result", "Your order was successfully placed.");
            session.removeAttribute("cart");
        } else {
            model.addAttribute("result", "There was an error placing your order.  " +
                    "Please try again later.  The error was " + result);
        }

        model.addAttribute("order", order);
        return "orderResult";
    }

    @GetMapping("/payments")
    public String getAllPayments(Model model) {

        Payment[] payments = restTemplate.getForObject(
                paymentHistoryEndpoint + "/payments", Payment[].class);
        if (payments != null) {
            LOG.debug(String.format("All payments found = [%s]", payments.toString()));
        }
        model.addAttribute("payments", payments);
        return "payments";
    }

    @GetMapping("/payment/{id}")
    public String getPaymentById(@PathVariable("id") Long id, Model model) {

        Payment payment = restTemplate.getForObject(
                paymentHistoryEndpoint + "/payments/{id}", Payment.class, id);

        LOG.debug(String.format("Found payment [%s].", payment));
        model.addAttribute("payment", payment);
        return "paymentDetails";
    }

    private List<CatalogItem> getCart(HttpSession session) {
        if (session.getAttribute("cart") == null) {
            session.setAttribute("cart", new ArrayList<CatalogItem>());
        }

        @SuppressWarnings("unchecked")
        List<CatalogItem> cart = (List<CatalogItem>) session.getAttribute("cart");

        return cart;
    }

    private Double getCartTotal(HttpSession session) {

        List<CatalogItem> cart = this.getCart(session);
        double orderTotal = 0d;
        for (CatalogItem item : cart) {
            if (item != null && item.getAmount() != null) {
                orderTotal += item.getAmount().doubleValue();
            }
        }

        return Double.valueOf(orderTotal);
    }
}
