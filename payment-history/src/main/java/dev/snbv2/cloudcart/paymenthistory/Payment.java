package dev.snbv2.cloudcart.paymenthistory;

import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * JPA entity representing a payment transaction record stored in the payments table.
 */
@Getter
@Setter
@Entity
@Table(name="payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private String cardNumber;

    @Column
    private String cvc;

    @Column
    private Integer expirationMonth;

    @Column
    private Integer expirationYear;

    @Column
    private BigDecimal amount;

    @Column
    private String currency;

    @Column
    private String description;

    /**
     * Default no-argument constructor required by JPA.
     */
    public Payment() {
    }

    /**
     * Constructs a Payment with all fields specified.
     *
     * @param id the unique identifier
     * @param cardNumber the payment card number
     * @param cvc the card verification code
     * @param expirationMonth the card expiration month
     * @param expirationYear the card expiration year
     * @param amount the payment amount
     * @param currency the currency code for the payment
     * @param description a description of the payment
     */
    public Payment(Integer id, String cardNumber, String cvc, Integer expirationMonth, Integer expirationYear,
            BigDecimal amount, String currency, String description) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.cvc = cvc;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Payment [amount=" + amount + ", cardNumber=" + "****" + (cardNumber != null && cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : "") + ", currency=" + currency + ", cvc=" + "***"
                + ", description=" + description + ", expirationMonth=" + expirationMonth + ", expirationYear="
                + expirationYear + ", id=" + id + "]";
    }

}
