package dev.snbv2.cloudcart.frontend;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a summary of a placed order including customer information, amount, and result status.
 */
@Getter
@Setter
@ToString
public class OrderSummary {

    private Integer id;
    private String firstName;
    private String lastName;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String zipCode;
    private BigDecimal amount;
    private String result;

    private List<CatalogItem> catalogItems;

    /**
     * Constructs an empty order summary.
     */
   public OrderSummary() {
    }

}
