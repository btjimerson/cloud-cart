package dev.snbv2.cloudcart.catalog;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class CatalogRepositoryTests {

    private static final Log LOG = LogFactory.getLog(CatalogRepositoryTests.class);

    @Autowired
    CatalogRepository catalogRepository;

    @Test
    void testFindAll() throws Exception {

        List<CatalogItem> catalogItems = new ArrayList<>();
        for (CatalogItem catalogItem : catalogRepository.findAll()) {
            catalogItems.add(catalogItem);
        }

        LOG.info(String.format("All catalog items = [%s]", catalogItems));

        Assertions.assertFalse(catalogItems.isEmpty());
        CatalogItem catalogItem = catalogItems.get(0);
        Assertions.assertNotNull(catalogItem.getId());
        Assertions.assertNotNull(catalogItem.getName());
        Assertions.assertNotNull(catalogItem.getDescription());
        Assertions.assertNotNull(catalogItem.getAmount());
    }

    @Test
    void testIdTypes() {
        for (CatalogItem catalogItem : catalogRepository.findAll()) {

            LOG.info(String.format("ID type = [%s]", catalogItem.getId().getClass()));
            Assertions.assertInstanceOf(Integer.class, catalogItem.getId());

            LOG.info(String.format("ID as string type = [%s]", catalogItem.getIdAsString().getClass()));
            Assertions.assertInstanceOf(String.class, catalogItem.getIdAsString());
        }
    }
}
