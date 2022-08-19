package com.example.orders;

import capital.scalable.restdocs.AutoDocumentation;
import capital.scalable.restdocs.jackson.JacksonResultHandlers;
import capital.scalable.restdocs.response.ResponseModifyingPreprocessors;
import com.example.orders.controllers.OrderController;
import com.example.orders.db.filter.enums.FieldType;
import com.example.orders.db.filter.enums.Operator;
import com.example.orders.db.filter.enums.SortDirection;
import com.example.orders.db.filter.requests.FilterRequest;
import com.example.orders.db.filter.requests.SearchRequest;
import com.example.orders.db.filter.requests.SortRequest;
import com.example.orders.dto.OrderDTO;
import com.example.orders.models.Order;
import com.example.orders.models.enums.OrderState;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
class OrdersHttpApiWithDocsTests {

    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderController orderController;

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .alwaysDo(JacksonResultHandlers.prepareJackson(objectMapper))
                .alwaysDo(MockMvcRestDocumentation.document("{method-name}",
                        Preprocessors.preprocessRequest(),
                        Preprocessors.preprocessResponse(
                                ResponseModifyingPreprocessors.replaceBinaryContent(),
                                ResponseModifyingPreprocessors.limitJsonArrayLength(objectMapper),
                                Preprocessors.prettyPrint())))
                .apply(MockMvcRestDocumentation
                        .documentationConfiguration(restDocumentation)
                        .uris()
                        .withScheme("http")
                        .withHost("localhost")
                        .withPort(8081)
                        .and().snippets()
                        .withDefaults(CliDocumentation.curlRequest(),
                                HttpDocumentation.httpRequest(),
                                HttpDocumentation.httpResponse(),
                                AutoDocumentation.requestFields(),
                                AutoDocumentation.responseFields(),
                                AutoDocumentation.pathParameters(),
                                AutoDocumentation.requestParameters(),
                                AutoDocumentation.description(),
                                AutoDocumentation.methodAndPath(),
                                AutoDocumentation.section()))
                .build();

    }


    @Test
    void testSaveOrder() throws Exception {
        OrderDTO orderDTO = addOrderDTO();
        String orderDTOJson = new ObjectMapper().writeValueAsString(orderDTO);
        MvcResult result = mockMvc.perform(
                post("/api/orders/create")
                        .content(orderDTOJson)
                        .contentType("application/json"))
                .andExpect(status().isCreated())
                .andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assertions.assertEquals(orderResponseJson(), resultContent);
    }

    @Test
    void testGetOrder() throws Exception {
        OrderDTO orderDTO = addOrderDTO();
        Order order = orderDtoToOrder(orderDTO);
        MvcResult result = mockMvc.perform(
                        get("/api/orders/"+order.getId()))
                .andExpect(status().isOk())
                .andReturn();
        String resultContent = result.getResponse().getContentAsString();
        String orderJSON = new ObjectMapper().writeValueAsString(orderController.getOrder(order.getId()).getBody());
        Assertions.assertEquals(orderJSON, resultContent);
    }

    @Test
    void testCancelOrder() throws Exception {
        OrderDTO orderDTO = addOrderDTO();
        Order order = orderDtoToOrder(orderDTO);
        MvcResult result = mockMvc.perform(
                        put("/api/orders/"+order.getId()+"/cancel"))
                .andExpect(status().isOk())
                .andReturn();
        String resultContent = result.getResponse().getContentAsString();
        String orderJSON = new ObjectMapper().writeValueAsString(orderController.getOrder(order.getId()).getBody());
        Assertions.assertEquals(orderJSON, resultContent);
    }

    @Test
    void testStartOrder() throws Exception {
        OrderDTO orderDTO = addOrderDTO();
        Order order = orderDtoToOrder(orderDTO);
        MvcResult result = mockMvc.perform(
                        put("/api/orders/"+order.getId()+"/start"))
                .andExpect(status().isOk())
                .andReturn();
        String resultContent = result.getResponse().getContentAsString();
        String orderJSON = new ObjectMapper().writeValueAsString(orderController.getOrder(order.getId()).getBody());
        Assertions.assertEquals(orderJSON, resultContent);
    }

    @Test
    void testCompleteOrder() throws Exception {
        OrderDTO orderDTO = addOrderDTO();
        Order order = orderDtoToOrder(orderDTO);
        MvcResult result = mockMvc.perform(
                        put("/api/orders/"+order.getId()+"/complete"))
                .andExpect(status().isOk())
                .andReturn();
        String resultContent = result.getResponse().getContentAsString();
        String orderJSON = new ObjectMapper().writeValueAsString(orderController.getOrder(order.getId()).getBody());
        Assertions.assertEquals(orderJSON, resultContent);
    }

    @Test
    void testEditOrder() throws Exception {
        OrderDTO orderDTO = addOrderDTO();
        String orderDTOJson = new ObjectMapper().writeValueAsString(orderDTO);
        MvcResult result = mockMvc.perform(
                        put("/api/orders/"+6)
                                .content(orderDTOJson)
                                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assertions.assertEquals(orderResponseJson(), resultContent);
    }

    @Test
    void testDeleteOrder() throws Exception {
        OrderDTO orderDTO = addOrderDTO();
        Order order = orderDtoToOrder(orderDTO);
        MvcResult result = mockMvc.perform(
                        delete("/api/orders/"+7))
                .andExpect(status().isOk())
                .andReturn();
        String resultContent = result.getResponse().getContentAsString();
        Assertions.assertEquals("Order is deleted", resultContent);
    }

    @Test
    void testSearch() throws Exception {
        FilterRequest filterRequest = new FilterRequest();
        filterRequest.setKey("customer");
        filterRequest.setOperator(Operator.EQUAL);
        filterRequest.setFieldType(FieldType.STRING);
        filterRequest.setValue("Nurbolat Amilbek");

        SortRequest sortRequest = new SortRequest();
        sortRequest.setKey("costing");
        sortRequest.setDirection(SortDirection.ASC);

        List<FilterRequest> filterRequests = new ArrayList<>();
        filterRequests.add(filterRequest);

        List<SortRequest> sortRequests = new ArrayList<>();
        sortRequests.add(sortRequest);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setFilters(filterRequests);
        searchRequest.setSorts(sortRequests);
        searchRequest.setPage(null);
        searchRequest.setSize(null);

        String searchRequestJson = new ObjectMapper().writeValueAsString(searchRequest);

        MvcResult result = mockMvc.perform(
                        post("/api/orders/search")
                                .content(searchRequestJson)
                                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        String responseSON = new ObjectMapper().writeValueAsString(orderController.search(searchRequest).getBody());
        Assertions.assertEquals(responseSON, resultContent);
    }

    private OrderDTO addOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setName("name");
        orderDTO.setCosting(BigInteger.valueOf(123));
        orderDTO.setAddressTo("address to");
        orderDTO.setCustomer("customer");
        return orderDTO;
    }

    private Order orderDtoToOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setId(5L);
        order.setName(orderDTO.getName());
        order.setAddressTo(orderDTO.getAddressTo());
        order.setCosting(orderDTO.getCosting());
        order.setCustomer(orderDTO.getCustomer());
        order.setOrderState(OrderState.NEW);
        return order;
    }

    private String orderResponseJson() {
        return "{" +
                "\n  \"name\" : \"name\"," +
                "\n  \"addressTo\" : \"address\"," +
                "\n  \"costing\" : 12," +
                "\n  \"customer\" : \"customer\"," +
                "\n  \"orderState\" : \"NEW\"" +
                "\n}";
    }
}
