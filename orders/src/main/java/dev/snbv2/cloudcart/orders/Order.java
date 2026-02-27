package dev.snbv2.cloudcart.orders;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * JPA entity representing a customer order, mapped to the "orders" database table.
 */
@Getter
@Setter
@ToString
@Entity
@Table(name="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String firstName;
    private String lastName;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String zipCode;
    private BigDecimal amount;
    private Integer catalogItemId;

    /**
     * Default no-argument constructor required by JPA.
     */
    public Order() {
    }

    /**
     * Constructs an order with all fields specified.
     *
     * @param id the order ID
     * @param firstName the customer's first name
     * @param lastName the customer's last name
     * @param address the primary shipping address
     * @param address2 the secondary shipping address line
     * @param city the shipping city
     * @param state the shipping state
     * @param zipCode the shipping zip code
     * @param amount the order total amount
     * @param catalogItemId the ID of the catalog item ordered
     */
    public Order(Integer id, String firstName, String lastName, String address, String address2, String city, String state,
            String zipCode, BigDecimal amount, Integer catalogItemId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.amount = amount;
        this.catalogItemId = catalogItemId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
