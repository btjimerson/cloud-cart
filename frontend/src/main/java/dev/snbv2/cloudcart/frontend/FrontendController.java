package dev.snbv2.cloudcart.frontend;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

/**
 * Spring MVC controller that handles web UI requests for browsing the catalog,
 * managing the shopping cart, placing orders, and viewing payment history.
 */
@CommonsLog
@Controller
public class FrontendController {

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
     * Constructs a FrontendController with the given RestClient.
     *
     * @param restClient the RestClient used to communicate with backend services
     */
    public FrontendController(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Populates the application version model attribute for every request.
     *
     * @param model the Spring MVC model
     */
    @ModelAttribute
    public void populateAppVersion(Model model) {
        log.debug(String.format("Setting app version attribute to [%s]", appVersion));
        model.addAttribute("appVersion", appVersion);
    }

    /**
     * Renders the index (home) page.
     *
     * @return the index view name
     */
    @GetMapping({ "/", "/index" })
    public String index() {
        return "index";
    }

    /**
     * Retrieves a single catalog item by its identifier and displays its details.
     *
     * @param id the catalog item identifier
     * @param model the Spring MVC model
     * @return the item details view name
     */
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

    /**
     * Retrieves all catalog items from the catalog service and displays them.
     *
     * @param model the Spring MVC model
     * @return the catalog view name
     */
    @GetMapping("/catalog")
    public String getAllCatalogItems(Model model) {

        CatalogItem[] catalogItems = restClient.get()
                .uri(catalogEndpoint + "/catalog")
                .retrieve()
                .body(CatalogItem[].class);

        model.addAttribute("catalogItems", catalogItems);
        return "catalog";
    }

    /**
     * Adds a catalog item to the shopping cart stored in the user's session.
     *
     * @param id the catalog item identifier to add
     * @param model the Spring MVC model
     * @param session the HTTP session containing the cart
     * @return the catalog view name
     */
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

    /**
     * Displays the checkout page with the current cart contents and order total.
     *
     * @param model the Spring MVC model
     * @param session the HTTP session containing the cart
     * @return the checkout view name
     */
    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {

        Order order = new Order();
        order.setCatalogItems(this.getCart(session));
        order.setOrderTotal(this.getCartTotal(session));
        log.debug(String.format("Order = [%s]", order));

        model.addAttribute("order", order);
        return "checkout";
    }

    /**
     * Processes the submitted order by sending payment and persisting the order summary.
     *
     * @param order the order submitted from the checkout form
     * @param model the Spring MVC model
     * @param session the HTTP session containing the cart
     * @return the order result view name
     */
    @PostMapping("/order")
    public String placeOrder(Order order, Model model, HttpSession session) {

        log.debug(String.format("Order being placed = [%s]", order));

        order.getPayment().setCurrency("usd");
        order.getPayment().setAmount(this.getCartTotal(session));
        order.getPayment().setDescription(String.format("Order placed on %s", Instant.now()));

        String result = restClient.post()
                .uri(paymentsEndpoint + "/payment")
                .body(order.getPayment())
                .retrieve()
                .body(String.class);

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

        OrderSummary savedOrder = restClient.post()
                .uri(ordersEndpoint + "/order")
                .body(orderSummary)
                .retrieve()
                .body(OrderSummary.class);

        log.debug(String.format("Result of order = [%s]", savedOrder));

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

    /**
     * Retrieves all payments from the payment history service and displays them.
     *
     * @param model the Spring MVC model
     * @return the payments view name
     */
    @GetMapping("/payments")
    public String getAllPayments(Model model) {

        Payment[] payments = restClient.get()
                .uri(paymentHistoryEndpoint + "/payments")
                .retrieve()
                .body(Payment[].class);
        if (payments != null) {
            log.debug(String.format("All payments found = [%s]", Arrays.toString(payments)));
        }
        model.addAttribute("payments", payments);
        return "payments";
    }

    /**
     * Retrieves a single payment by its identifier and displays its details.
     *
     * @param id the payment identifier
     * @param model the Spring MVC model
     * @return the payment details view name
     */
    @GetMapping("/payment/{id}")
    public String getPaymentById(@PathVariable("id") Long id, Model model) {

        Payment payment = restClient.get()
                .uri(paymentHistoryEndpoint + "/payments/{id}", id)
                .retrieve()
                .body(Payment.class);

        log.debug(String.format("Found payment [%s].", payment));
        model.addAttribute("payment", payment);
        return "paymentDetails";
    }

    /**
     * Retrieves the shopping cart from the session, creating one if it does not exist.
     *
     * @param session the HTTP session
     * @return the list of catalog items in the cart
     */
    private List<CatalogItem> getCart(HttpSession session) {
        if (session.getAttribute("cart") == null) {
            session.setAttribute("cart", new ArrayList<CatalogItem>());
        }

        @SuppressWarnings("unchecked")
        List<CatalogItem> cart = (List<CatalogItem>) session.getAttribute("cart");

        return cart;
    }

    /**
     * Calculates the total price of all items in the shopping cart.
     *
     * @param session the HTTP session containing the cart
     * @return the total amount of all items in the cart
     */
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
