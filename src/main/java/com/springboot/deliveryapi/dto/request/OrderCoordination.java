package com.springboot.deliveryapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderCoordination {
    @NotEmpty(message = "Origin or destination field is missing")
    @Size(min = 2, max = 2, message = "Origin or destination has invalid size")
    List<String> origin;

    @NotEmpty(message = "Origin or destination field is missing")
    @Size(min = 2, max = 2, message = "Origin or destination has invalid size")
    List<String> destination;
}
