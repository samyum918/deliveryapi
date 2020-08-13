package com.springboot.deliveryapi.controller;

import com.springboot.deliveryapi.repository.OrdersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@Sql({"/schema.sql", "/data.sql"})
public class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrdersRepository ordersRepository;


    //Place order API
    @Test
    public void placeOrder_whenWrongDataInput_thenReturn400() throws Exception {
        String requestBody = "{\n" +
                "  \"origin\": [\n" +
                "    \"22.3352484\"\n" +
                "  ],\n" +
                "  \"destination\": [\n" +
                "    \"22.3263164\"\n" +
                "  ]\n" +
                "}";
        MvcResult result = submitPlaceOrderRequest(requestBody);
        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void placeOrder_whenLatitudeInvalid_thenReturn400() throws Exception {
        String requestBody = "{\n" +
                "  \"origin\": [\n" +
                "    \"-90.1\",\n" +
                "    \"114.2046599\"\n" +
                "  ],\n" +
                "  \"destination\": [\n" +
                "    \"99.0\",\n" +
                "    \"114.2045314\"\n" +
                "  ]\n" +
                "}";
        MvcResult result = submitPlaceOrderRequest(requestBody);
        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void placeOrder_whenLongitudeInvalid_thenReturn400() throws Exception {
        String requestBody = "{\n" +
                "  \"origin\": [\n" +
                "    \"22.3352484\",\n" +
                "    \"-180.1\"\n" +
                "  ],\n" +
                "  \"destination\": [\n" +
                "    \"22.3263164\",\n" +
                "    \"114.2045314\"\n" +
                "  ]\n" +
                "}";
        MvcResult result = submitPlaceOrderRequest(requestBody);
        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void placeOrder_whenEverythingCorrect_thenReturn200() throws Exception {
        String requestBody = "{\n" +
                "  \"origin\": [\n" +
                "    \"22.3352484\",\n" +
                "    \"114.2046599\"\n" +
                "  ],\n" +
                "  \"destination\": [\n" +
                "    \"22.3263164\",\n" +
                "    \"114.2045314\"\n" +
                "  ]\n" +
                "}";
        MvcResult result = submitPlaceOrderRequest(requestBody);
        assertEquals(200, result.getResponse().getStatus());
    }


    //take order API
    @Test
    public void takeOrder_whenOrderAlreadyAssigned_thenReturn403() throws Exception {
        MvcResult result = submitTakeOrderRequest("1");
        assertEquals(403, result.getResponse().getStatus());
    }

    @Test
    public void takeOrder_whenEverythingCorrect_thenReturn200() throws Exception {
        MvcResult result = submitTakeOrderRequest("2");
        System.out.println(result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }


    // orders API
    @Test
    public void ordersList_whenPageLessThanOne_thenReturn400() throws Exception {
        MvcResult result = submitOrdersListRequest("0", "10");
        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void ordersList_whenLimitInvalid_thenReturn400() throws Exception {
        MvcResult result = submitOrdersListRequest("1", "abc");
        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void ordersList_whenEverythingCorrect_thenReturn200() throws Exception {
        MvcResult result = submitOrdersListRequest("1", "10");
        System.out.println(result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }


    public MvcResult submitPlaceOrderRequest(String requestBody) throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);
        return mockMvc.perform(request).andReturn();
    }

    public MvcResult submitTakeOrderRequest(String id) throws Exception {
        String requestBody = "{\n" +
                "    \"status\": \"TAKEN\"\n" +
                "}";
        RequestBuilder request = MockMvcRequestBuilders.patch("/orders/" + id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody);
        return mockMvc.perform(request).andReturn();
    }

    public MvcResult submitOrdersListRequest(String page, String limit) throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/orders?page=" + page + "&limit=" + limit);
        return mockMvc.perform(request).andReturn();
    }
}
