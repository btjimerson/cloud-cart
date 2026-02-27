package dev.snbv2.cloudcart.frontend;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a customer's billing address including name, email, and mailing address fields.
 */
@Getter
@Setter
@ToString
public class BillingAddress {

    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String zipCode;

    /**
     * Constructs an empty billing address.
     */
    public BillingAddress() {
    }

    /**
     * Constructs a billing address with the specified properties.
     *
     * @param firstName the customer's first name
     * @param lastName the customer's last name
     * @param email the customer's email address
     * @param address the primary address line
     * @param address2 the secondary address line
     * @param city the city
     * @param state the state
     * @param zipCode the ZIP code
     */
    public BillingAddress(String firstName, String lastName, String email, String address, String address2,
            String city, String state, String zipCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

}
