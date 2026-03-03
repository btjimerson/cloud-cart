package dev.snbv2.cloudcart.payments;

import java.math.BigDecimal;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a credit card payment with card details, amount, and currency information.
 */
@Getter
@Setter
public class Payment {

    private String cardNumber;
    private String cvc;
    private Integer expirationMonth;
    private Integer expirationYear;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String correlationId;

    public Payment() {

    }

    public Payment(String cardNumber, String cvc, Integer expirationMonth, Integer expirationYear, BigDecimal amount,
            String currency, String description, String correlationId) {
        this.cardNumber = cardNumber;
        this.cvc = cvc;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.correlationId = correlationId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(cardNumber, payment.cardNumber)
                && Objects.equals(cvc, payment.cvc)
                && Objects.equals(expirationMonth, payment.expirationMonth)
                && Objects.equals(expirationYear, payment.expirationYear)
                && Objects.equals(amount, payment.amount)
                && Objects.equals(currency, payment.currency)
                && Objects.equals(description, payment.description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(cardNumber, cvc, expirationMonth, expirationYear, amount, currency, description);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "CardPayment [amount=" + amount + ", cardNumber=" + "****" + (cardNumber != null && cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : "") + ", currency=" + currency + ", cvc="
                + "***" + ", description=" + description + ", expirationMonth=" + expirationMonth + ", expirationYear="
                + expirationYear + "]";
    }

}
