package dev.snbv2.cloudcart.payments;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PaymentsApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(PaymentsApplication.class);
		app.addListeners(new EnvironmentValidationListener());
		app.run(args);
	}

	@Bean
	public FanoutExchange paymentsExchange() {
		return new FanoutExchange("payments");
	}

}
