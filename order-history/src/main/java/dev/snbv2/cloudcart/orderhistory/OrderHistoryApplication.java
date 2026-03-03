package dev.snbv2.cloudcart.orderhistory;

import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OrderHistoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderHistoryApplication.class, args);
	}

	@Bean
	FanoutExchange ordersExchange() {
		return new FanoutExchange("orders");
	}

	@Bean
	FanoutExchange paymentsExchange() {
		return new FanoutExchange("payments");
	}

	@Bean
	Queue ordersQueue() {
		return new AnonymousQueue();
	}

	@Bean
	Queue paymentsQueue() {
		return new AnonymousQueue();
	}

	@Bean
	Binding ordersBinding(FanoutExchange ordersExchange, Queue ordersQueue) {
		return BindingBuilder.bind(ordersQueue).to(ordersExchange);
	}

	@Bean
	Binding paymentsBinding(FanoutExchange paymentsExchange, Queue paymentsQueue) {
		return BindingBuilder.bind(paymentsQueue).to(paymentsExchange);
	}

	@Bean
	MessageListenerAdapter orderListenerAdapter(OrderHistoryMessageReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receiveOrderEvent");
	}

	@Bean
	MessageListenerAdapter paymentListenerAdapter(OrderHistoryMessageReceiver receiver) {
		return new MessageListenerAdapter(receiver, "receivePaymentEvent");
	}

	@Bean
	SimpleMessageListenerContainer ordersContainer(
			ConnectionFactory connectionFactory,
			MessageListenerAdapter orderListenerAdapter,
			Queue ordersQueue) {

		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(ordersQueue.getName());
		container.setMessageListener(orderListenerAdapter);
		return container;
	}

	@Bean
	SimpleMessageListenerContainer paymentsContainer(
			ConnectionFactory connectionFactory,
			MessageListenerAdapter paymentListenerAdapter,
			Queue paymentsQueue) {

		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames(paymentsQueue.getName());
		container.setMessageListener(paymentListenerAdapter);
		return container;
	}

}
