package dev.snbv2.cloudcart.catalog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@DataJpaTest
@Import(DataSeeder.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class DataSeederTests {

    @Autowired
    CatalogRepository catalogRepository;

    @Test
    void testSeederLoads50Products() {
        List<CatalogItem> items = new ArrayList<>();
        catalogRepository.findAll().forEach(items::add);
        Assertions.assertEquals(50, items.size());
    }

    @Test
    void testSeededProductFieldsArePopulated() {
        CatalogItem item = catalogRepository.findById(100).orElse(null);
        Assertions.assertNotNull(item);
        Assertions.assertEquals("Essence Mascara Lash Princess", item.getName());
        Assertions.assertEquals(0, BigDecimal.valueOf(9.99).compareTo(item.getAmount()));
        Assertions.assertNotNull(item.getImageSource());
        Assertions.assertNotNull(item.getDescription());
    }

    @Test
    void testSeededProductIds() {
        // Verify first and last IDs are present
        Assertions.assertTrue(catalogRepository.findById(100).isPresent());
        Assertions.assertTrue(catalogRepository.findById(5000).isPresent());
        // Verify IDs increment by 100
        Assertions.assertTrue(catalogRepository.findById(2500).isPresent());
        Assertions.assertFalse(catalogRepository.findById(150).isPresent());
    }
}
