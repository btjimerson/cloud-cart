package dev.snbv2.cloudcart.orders;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OrdersApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrdersApplication.class, args);
	}

	@Bean
	public FanoutExchange ordersExchange() {
		return new FanoutExchange("orders");
	}

}
