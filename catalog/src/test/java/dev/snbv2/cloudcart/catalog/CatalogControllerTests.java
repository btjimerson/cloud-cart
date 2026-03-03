package dev.snbv2.cloudcart.catalog;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class CatalogControllerTests {

    @Autowired
    WebApplicationContext context;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testGetCatalogItems() throws Exception {
        mvc.perform(get("/catalog"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").isNotEmpty());
    }

    @Test
    void testGetCatalogItemsReturns50Products() throws Exception {
        mvc.perform(get("/catalog"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(50));
    }

    @Test
    void testGetCatalogItemById() throws Exception {
        mvc.perform(get("/catalog/100"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(100))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Essence Mascara Lash Princess"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(9.99))
            .andExpect(MockMvcResultMatchers.jsonPath("$.imageSource").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").isNotEmpty());
    }

    @Test
    void testGetCatalogItemByIdNotFound() throws Exception {
        mvc.perform(get("/catalog/99999"))
            .andExpect(status().isNotFound());
    }
}
