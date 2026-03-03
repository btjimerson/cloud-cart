package dev.snbv2.cloudcart.orders;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@SpringBootTest
class OrdersApplicationTests {

	@MockBean
	RabbitTemplate rabbitTemplate;

	@Test
	void contextLoads() {
	}

}
