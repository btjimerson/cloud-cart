package dev.snbv2.cloudcart.frontend;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a payment with card details, amount, currency, and description.
 */
@Getter
@Setter
public class Payment {

    private Integer id;
    private String cardNumber;
    private String cvc;
    private Integer expirationMonth;
    private Integer expirationYear;
    private BigDecimal amount;
    private String currency;
    private String description;

    /**
     * Constructs an empty payment.
     */
    public Payment() {

    }

    /**
     * Constructs a payment with the specified properties.
     *
     * @param id the unique identifier of the payment
     * @param cardNumber the credit card number
     * @param cvc the card verification code
     * @param expirationMonth the card expiration month
     * @param expirationYear the card expiration year
     * @param amount the payment amount
     * @param currency the currency code
     * @param description a description of the payment
     */
    public Payment(Integer id, String cardNumber, String cvc, Integer expirationMonth, Integer expirationYear, BigDecimal amount,
            String currency, String description) {
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
    public String toString() {
        return "CardPayment [amount=" + amount + ", cardNumber=" + "****" + (cardNumber != null && cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : "") + ", currency=" + currency + ", cvc="
                + "***" + ", description=" + description + ", expirationMonth=" + expirationMonth + ", expirationYear="
                + expirationYear + "]";
    }

}
