package com.springboot.deliveryapi.controller;

import com.springboot.deliveryapi.repository.OrdersRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static junit.framework.TestCase.assertEquals;

@RunWith(SpringRunner.class)
@WebMvcTest(DeliveryController.class)
public class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrdersRepository ordersRepository;

    @Test
    public void placeOrder_whenWrongFormat_thenReturn400() throws Exception {
        String requestBody = "{\n" +
                "  \"origin\": [\n" +
                "    \"22.3352484\"\n" +
                "  ],\n" +
                "  \"destination\": [\n" +
                "    \"22.3263164\"\n" +
                "  ]\n" +
                "}";
        MvcResult result = submitPlaceOrderRequest(requestBody);
        assertEquals(result.getResponse().getStatus(), 400);
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
        assertEquals(result.getResponse().getStatus(), 400);
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
        assertEquals(result.getResponse().getStatus(), 400);
    }


    public MvcResult submitPlaceOrderRequest(String requestBody) throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);
        return mockMvc.perform(request).andReturn();
    }
}
