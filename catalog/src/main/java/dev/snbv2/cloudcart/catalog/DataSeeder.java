package dev.snbv2.cloudcart.catalog;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;

/**
 * Component that seeds the catalog database with initial data from a JSON file on application startup.
 */
@CommonsLog
@Component
public class DataSeeder implements CommandLineRunner {

    private final CatalogRepository catalogRepository;

    /**
     * Constructs a DataSeeder with the given catalog repository.
     *
     * @param catalogRepository the repository used to persist seed data
     */
    public DataSeeder(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    /**
     * Reads catalog seed data from the classpath JSON file and saves each item to the repository.
     *
     * @param args command-line arguments (not used)
     * @throws Exception if the seed data file cannot be read or parsed
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("Loading catalog seed data.");

        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = new ClassPathResource("seed-data/catalog.json").getInputStream()) {
            JsonNode rootNode = objectMapper.readTree(inputStream);

            for (JsonNode node : rootNode) {
                CatalogItem item = new CatalogItem();
                item.setId(node.get("id").asInt());
                item.setName(node.get("name").asText());
                item.setDescription(node.get("description").asText());
                item.setAmount(BigDecimal.valueOf(node.get("amount").asDouble()));
                item.setImageSource(node.get("imageSource").asText());
                catalogRepository.save(item);
            }
        }

        log.info("Finished loading catalog seed data.");
    }
}
