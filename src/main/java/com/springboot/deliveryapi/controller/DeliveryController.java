package com.springboot.deliveryapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.LatLng;
import com.google.maps.model.Unit;
import com.springboot.deliveryapi.dto.request.OrderCoordination;
import com.springboot.deliveryapi.model.enums.Status;
import com.springboot.deliveryapi.exception.ApiBadRequestException;
import com.springboot.deliveryapi.exception.ApiForbiddenException;
import com.springboot.deliveryapi.exception.ApiResourceNotFoundException;
import com.springboot.deliveryapi.model.Orders;
import com.springboot.deliveryapi.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
public class DeliveryController {
    private static Logger logger = Logger.getLogger(DeliveryController.class.getName());

    @Value("${google.distance-matrix.api-key}")
    private String API_KEY;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    ObjectMapper objMapper;

    @PostMapping("/orders")
    public Orders placeOrder(@Valid @RequestBody OrderCoordination orderCoordination) {
        LatLng originLatLng = validateAndGetLatLng(orderCoordination.getOrigin().get(0), orderCoordination.getOrigin().get(1));
        LatLng destinationLatLng = validateAndGetLatLng(orderCoordination.getDestination().get(0), orderCoordination.getDestination().get(1));

        //Distance Matrix API
        //e.g. 22.3352484,114.2046599/22.3263164,114.2045314
        Long distance = 0l;
        try {
            distance = calculateDistance(originLatLng, destinationLatLng);
        }
        catch(Exception ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
            throw new ApiForbiddenException("Distance calculation encounters error");
        }

        //save new order
        Orders order = new Orders();
        order.setDistance(distance.intValue());
        order.setStatus(Status.UNASSIGNED);
        return ordersRepository.save(order);
    }


    @PatchMapping("/orders/{id}")
    public ObjectNode takeOrder(@PathVariable Integer id) {
        Optional<Orders> orderOpt = ordersRepository.findById(id);
        Orders targetOrder;
        if(orderOpt.isPresent()) {
            targetOrder = orderOpt.get();
        }
        else {
            throw new ApiResourceNotFoundException("Order cannot be found");
        }
        if(targetOrder.getStatus() != Status.UNASSIGNED) {
            throw new ApiForbiddenException("Order has been taken");
        }

        //update order
        targetOrder.setStatus(Status.ASSIGNED);
        ordersRepository.save(targetOrder);

        ObjectNode objectNode = objMapper.createObjectNode();
        objectNode.put("status", "SUCCESS");
        return objectNode;
    }


    @GetMapping("/orders")
    public List<Orders> ordersList(@RequestParam(defaultValue = "1", required = false) Integer page,
                                   @RequestParam(defaultValue = "10", required = false) Integer limit) {
        if(page < 1 || limit < 1) {
            throw new ApiBadRequestException("Page and limit must be greater than or equal to 1");
        }

        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Orders> ordersList = ordersRepository.findAll(pageable);
        return ordersList.getContent();
    }


    private LatLng validateAndGetLatLng(String LatitudeStr, String LongitudeStr) {
        double latitude, longitude;
        try {
            latitude = Double.parseDouble(LatitudeStr);
            longitude = Double.parseDouble(LongitudeStr);
        }
        catch (Exception ex) {
            throw new ApiBadRequestException("Origin or destination has non-numeric value");
        }
        if(latitude < -90d || latitude > 90d || longitude < -180d || longitude > 180d) {
            throw new ApiBadRequestException("Latitude or Longitude is out of range");
        }
        return new LatLng(latitude, longitude);
    }

    public long calculateDistance(LatLng originLatLng, LatLng destinationLatLng) throws Exception {
        GeoApiContext distCalcer = new GeoApiContext.Builder()
                                                .apiKey(API_KEY)
                                                .build();

        DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(distCalcer);
        DistanceMatrix result = req.origins(originLatLng)
                                .destinations(destinationLatLng)
                                .units(Unit.IMPERIAL)
                                .language("en-US")
                                .await();

        long distApart = result.rows[0].elements[0].distance.inMeters;

        return distApart;
    }
}
