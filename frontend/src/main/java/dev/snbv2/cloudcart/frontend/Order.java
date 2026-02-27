package dev.snbv2.cloudcart.frontend;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a customer order containing catalog items, a billing address, payment details, and a total.
 */
@Getter
@Setter
@ToString
public class Order {

    private List<CatalogItem> catalogItems;
    private BillingAddress billingAddress;
    private Payment payment;
    private BigDecimal orderTotal;

}
