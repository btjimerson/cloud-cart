package dev.snbv2.cloudcart.paymenthistory;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot application entry point for the Payment History service.
 * Configures RabbitMQ messaging infrastructure for receiving payment messages.
 */
@SpringBootApplication
public class PaymentHistoryApplication {

	/**
	 * Launches the Payment History Spring Boot application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(PaymentHistoryApplication.class, args);
	}

	/**
	 * Creates the RabbitMQ queue bean for the payments queue.
	 *
	 * @return a new Queue instance bound to the "payments" queue name
	 */
	@Bean
	Queue queue() {
		return new Queue("payments");
	}

	/**
	 * Creates a message listener adapter that delegates to the payment message receiver.
	 *
	 * @param receiver the payment message receiver component
	 * @return a MessageListenerAdapter configured to invoke receiveMessage on the receiver
	 */
	@Bean
	MessageListenerAdapter listenerAdapter(PaymentMessageReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveMessage");
	}

	/**
	 * Creates and configures the RabbitMQ message listener container.
	 *
	 * @param connectionFactory the RabbitMQ connection factory
	 * @param listenerAdapter the message listener adapter for handling messages
	 * @return a configured SimpleMessageListenerContainer listening on the "payments" queue
	 */
	@Bean
	SimpleMessageListenerContainer container(
		ConnectionFactory connectionFactory,
		MessageListenerAdapter listenerAdapter) {

		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames("payments");
		container.setMessageListener(listenerAdapter);
		return container;
	}

}
