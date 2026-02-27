package dev.snbv2.cloudcart.payments;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot application entry point for the payments service.
 */
@SpringBootApplication
public class PaymentsApplication {

	/**
	 * Main entry point for the payments application.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(PaymentsApplication.class);
		app.addListeners(new EnvironmentValidationListener());
		app.run(args);
	}

	/**
	 * Creates the RabbitMQ queue bean used for payment messages.
	 *
	 * @return a new Queue instance named "payments"
	 */
	@Bean
	public Queue queue() {
		return new Queue("payments");
	}

}
