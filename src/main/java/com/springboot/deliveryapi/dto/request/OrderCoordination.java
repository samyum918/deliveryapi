package com.springboot.deliveryapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderCoordination {
    List<String> origin;
    List<String> destination;
}
