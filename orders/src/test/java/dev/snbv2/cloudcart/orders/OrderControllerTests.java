package dev.snbv2.cloudcart.orders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderControllerTests {

    @Autowired
    WebApplicationContext context;

    @MockBean
    OrderMessageService orderMessageService;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testGetAllOrders() throws Exception {
        mvc.perform(get("/orders"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk());
    }

    @Test
    void testPlaceOrder() throws Exception {
        String order = "{" +
            "\"firstName\": \"First name\"," +
            "\"lastName\": \"Last name\"," +
            "\"address\": \"Address\"," +
            "\"address2\": \"Address 2\"," +
            "\"city\": \"City\"," +
            "\"state\": \"State\"," +
            "\"zipCode\": \"Zip code\"," +
            "\"amount\": \"101.55\"," +
            "\"catalogItemId\": \"100\"," +
            "\"correlationId\": \"test-correlation-id\"" +
            "}";

        mvc.perform(post("/order")
            .contentType("application/json")
            .content(order))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty())
            .andExpect(MockMvcResultMatchers.jsonPath("$.correlationId").value("test-correlation-id"));
    }

    @Test
    void testGetOrderById() throws Exception {
        String order = "{" +
            "\"firstName\": \"Jane\"," +
            "\"lastName\": \"Doe\"," +
            "\"address\": \"123 Main St\"," +
            "\"city\": \"Springfield\"," +
            "\"state\": \"IL\"," +
            "\"zipCode\": \"62701\"," +
            "\"amount\": \"49.99\"," +
            "\"catalogItemId\": \"200\"," +
            "\"correlationId\": \"corr-123\"" +
            "}";

        MvcResult result = mvc.perform(post("/order")
            .contentType("application/json")
            .content(order))
            .andExpect(status().isOk())
            .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(result.getResponse().getContentAsString());
        int id = node.get("id").asInt();

        mvc.perform(get("/order/" + id))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Jane"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Doe"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.amount").value(49.99));
    }

    @Test
    void testGetOrderByIdNotFound() throws Exception {
        mvc.perform(get("/order/99999"))
            .andExpect(status().isNotFound());
    }
}
