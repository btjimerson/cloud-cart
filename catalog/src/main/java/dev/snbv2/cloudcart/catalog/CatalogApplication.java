package dev.snbv2.cloudcart.catalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot application entry point for the Catalog service.
 */
@SpringBootApplication
public class CatalogApplication {

	/**
	 * Starts the Catalog Spring Boot application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(CatalogApplication.class, args);
	}

}
