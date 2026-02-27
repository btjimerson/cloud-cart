package dev.snbv2.cloudcart.payments;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Listener that validates required environment variables early in the startup
 * lifecycle, before the application context is fully created. Logs actionable
 * warnings so developers know how to configure missing keys.
 */
public class EnvironmentValidationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private static final Log log = LogFactory.getLog(EnvironmentValidationListener.class);

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment env = event.getEnvironment();
        String stripeApiKey = env.getProperty("STRIPE_API_KEY");

        if (stripeApiKey == null || stripeApiKey.isBlank()) {
            log.warn("\n" +
                "╔══════════════════════════════════════════════════════════════╗\n" +
                "║  STRIPE_API_KEY is not set!                                 ║\n" +
                "║                                                             ║\n" +
                "║  To fix this:                                               ║\n" +
                "║    1. cp .env.example .env                                  ║\n" +
                "║    2. Edit .env and add your Stripe API key                 ║\n" +
                "║    3. Restart the application                               ║\n" +
                "║                                                             ║\n" +
                "║  Payments will fail until this is configured.               ║\n" +
                "╚══════════════════════════════════════════════════════════════╝");
        }
    }
}
