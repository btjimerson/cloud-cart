package dev.snbv2.cloudcart.payments;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;
import com.stripe.param.ChargeCreateParams;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service that processes payments through the Stripe API.
 */
@CommonsLog
@Getter
@Setter
@Service
public class PaymentService {

    @Value("#{environment.STRIPE_API_KEY}")
    String apiKey;

    /**
     * Processes a payment by creating a Stripe token from the card details and submitting a charge.
     *
     * @param payment the payment containing card and amount details
     * @return the charge status from Stripe
     */
    public String processPayment(Payment payment) {
        String key = this.getApiKey();
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("STRIPE_API_KEY is not set. " +
                "Copy .env.example to .env and add your Stripe API key.");
        }
        RequestOptions requestOptions = RequestOptions.builder()
                .setApiKey(key.trim())
                .build();

        Map<String, Object> card = new HashMap<>();
        card.put("cvc", payment.getCvc());
        card.put("number", payment.getCardNumber());
        card.put("exp_month", payment.getExpirationMonth());
        card.put("exp_year", payment.getExpirationYear());

        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("card", card);
        Token token = null;
        try {
            token = Token.create(cardParams, requestOptions);
        } catch (StripeException e) {
            log.error("Error creating token.", e);
            throw new RuntimeException(e);
        }

        ChargeCreateParams chargeParams = ChargeCreateParams.builder()
                .setAmount(payment.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                .setCurrency(payment.getCurrency())
                .setSource(token.getId())
                .build();
        Charge charge = null;
        try {
            charge = Charge.create(chargeParams, requestOptions);
        } catch (StripeException e) {
            log.error("Error creating charge.", e);
            throw new RuntimeException(e);
        }
        log.info(String.format("Charge create result = [%s]", charge.getStatus()));

        return charge.getStatus();
    }

}
