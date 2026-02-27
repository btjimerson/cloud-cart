package dev.snbv2.cloudcart.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;


/**
 * Spring Boot application entry point for the Cloud Cart frontend service.
 */
@SpringBootApplication
public class FrontendApplication {

	/**
	 * Main entry point that launches the Spring Boot application.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(FrontendApplication.class, args);
	}

	/**
	 * Creates a {@link RestClient} bean for making HTTP requests to backend services.
	 *
	 * @param builder the auto-configured RestClient builder
	 * @return a new {@link RestClient} instance
	 */
	@Bean
	public RestClient restClient(RestClient.Builder builder) {
		return builder.build();
	}

}
