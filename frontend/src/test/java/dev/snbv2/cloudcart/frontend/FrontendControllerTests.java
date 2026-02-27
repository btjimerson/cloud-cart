package dev.snbv2.cloudcart.frontend;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockRestServiceServer
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class FrontendControllerTests {

    @Autowired
    WebApplicationContext context;

    @Autowired
    MockRestServiceServer server;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testIndex() throws Exception {
        mvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"));
    }

    @Test
    void testGetAllCatalogItems() throws Exception {
        server.expect(requestTo("http://catalog:8080/catalog"))
            .andRespond(withSuccess(
                "[{\"id\":100,\"name\":\"Test Item\",\"imageSource\":\"http://img.jpg\",\"description\":\"Description\",\"amount\":29.99}]",
                MediaType.APPLICATION_JSON));

        mvc.perform(get("/catalog"))
            .andExpect(status().isOk())
            .andExpect(view().name("catalog"))
            .andExpect(model().attributeExists("catalogItems"));
    }

    @Test
    void testGetCatalogItem() throws Exception {
        server.expect(requestTo("http://catalog:8080/catalog/100"))
            .andRespond(withSuccess(
                "{\"id\":100,\"name\":\"Test Item\",\"imageSource\":\"http://img.jpg\",\"description\":\"Description\",\"amount\":29.99}",
                MediaType.APPLICATION_JSON));

        mvc.perform(get("/item/100"))
            .andExpect(status().isOk())
            .andExpect(view().name("itemDetails"))
            .andExpect(model().attributeExists("item"));
    }

    @Test
    void testCheckout() throws Exception {
        mvc.perform(get("/checkout"))
            .andExpect(status().isOk())
            .andExpect(view().name("checkout"))
            .andExpect(model().attributeExists("order"));
    }

    @Test
    void testGetAllPayments() throws Exception {
        server.expect(requestTo("http://payment-history:8080/payments"))
            .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        mvc.perform(get("/payments"))
            .andExpect(status().isOk())
            .andExpect(view().name("payments"))
            .andExpect(model().attributeExists("payments"));
    }
}
