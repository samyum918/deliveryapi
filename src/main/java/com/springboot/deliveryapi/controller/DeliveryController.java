package com.springboot.deliveryapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.springboot.deliveryapi.model.Orders;
import com.springboot.deliveryapi.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DeliveryController {
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    ObjectMapper objMapper;

    @PostMapping("/orders")
    public Orders placeOrder() {
        return null;
    }

    @PatchMapping("/orders/{id}")
    public ObjectNode takeOrder(@PathVariable String id) {
        ObjectNode objectNode = objMapper.createObjectNode();

        objectNode.put("status", "success");
        return objectNode;
    }

    @GetMapping("/orders")
    public List<Orders> ordersList(@RequestParam("page") String page, @RequestParam("limit") String limit) {
        return null;
    }
}
