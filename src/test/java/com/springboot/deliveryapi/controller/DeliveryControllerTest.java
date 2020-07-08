package com.springboot.deliveryapi.controller;

import com.springboot.deliveryapi.model.Orders;
import com.springboot.deliveryapi.model.enums.Status;
import com.springboot.deliveryapi.repository.OrdersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;


@ExtendWith(SpringExtension.class)
@WebMvcTest(DeliveryController.class)
public class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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
        Orders order = new Orders();
        order.setId(1);
        order.setDistance(123);
        order.setStatus(Status.ASSIGNED);
        Optional<Orders> orderOpt = Optional.of(order);

        when(ordersRepository.findById(anyInt())).thenReturn(orderOpt);

        MvcResult result = submitTakeOrderRequest("1");
        assertEquals(403, result.getResponse().getStatus());
    }

    @Test
    public void takeOrder_whenEverythingCorrect_thenReturn200() throws Exception {
        Orders order = new Orders();
        order.setId(1);
        order.setDistance(123);
        order.setStatus(Status.UNASSIGNED);
        Optional<Orders> orderOpt = Optional.of(order);

        when(ordersRepository.findById(anyInt())).thenReturn(orderOpt);
        when(ordersRepository.save(any(Orders.class))).thenReturn(order);

        MvcResult result = submitTakeOrderRequest("1");
        System.out.println(result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }


    // orders API
    @Test
    public void ordersList_whenPageLessThanOne_thenReturn400() throws Exception {
        setupOrdersListTest();
        MvcResult result = submitOrdersListRequest("0", "10");
        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void ordersList_whenLimitInvalid_thenReturn400() throws Exception {
        setupOrdersListTest();
        MvcResult result = submitOrdersListRequest("1", "abc");
        assertEquals(400, result.getResponse().getStatus());
    }

    @Test
    public void ordersList_whenEverythingCorrect_thenReturn200() throws Exception {
        setupOrdersListTest();
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

    public void setupOrdersListTest() {
        List<Orders> ordersList = new ArrayList<>();
        Orders order = new Orders();
        order.setId(1);
        order.setDistance(123);
        order.setStatus(Status.UNASSIGNED);
        ordersList.add(order);
        Page<Orders> pagedOrdersList = new PageImpl(ordersList);

        when(ordersRepository.findAll(any(Pageable.class))).thenReturn(pagedOrdersList);
    }
}
