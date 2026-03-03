package dev.snbv2.cloudcart.frontend;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockRestServiceServer
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class FrontendAPIControllerTests {

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
    void testGetAllCatalogItems() throws Exception {
        server.expect(requestTo("http://catalog:8080/catalog"))
            .andRespond(withSuccess(
                "[{\"id\":100,\"name\":\"Test Item\",\"imageSource\":\"http://img.jpg\",\"description\":\"Description\",\"amount\":29.99}]",
                MediaType.APPLICATION_JSON));

        mvc.perform(get("/api/catalog"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Test Item"));
    }

    @Test
    void testGetCatalogItemById() throws Exception {
        server.expect(requestTo("http://catalog:8080/catalog/100"))
            .andRespond(withSuccess(
                "{\"id\":100,\"name\":\"Test Item\",\"imageSource\":\"http://img.jpg\",\"description\":\"Description\",\"amount\":29.99}",
                MediaType.APPLICATION_JSON));

        mvc.perform(get("/api/catalog/100"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(100))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Test Item"));
    }

    @Test
    void testGetAllOrderHistory() throws Exception {
        server.expect(requestTo("http://order-history:8080/order-history"))
            .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        mvc.perform(get("/api/order-history"))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(0));
    }

    @Test
    void testGetAppVersion() throws Exception {
        mvc.perform(get("/api/version"))
            .andExpect(status().isOk());
    }
}
