package com.kostyanetskiy.deliveryservice.controller;

import com.kostyanetskiy.deliveryservice.dto.CourierCreateRequest;
import com.kostyanetskiy.deliveryservice.dto.CourierResponse;
import com.kostyanetskiy.deliveryservice.service.CourierService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/courier")
@SecurityRequirement(name = "basicAuth")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CourierResponse create(@RequestBody CourierCreateRequest courierCreateRequest) {
        return courierService.createCourier(courierCreateRequest);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<CourierResponse> allCouriers() {
        return courierService.allCouriers();
    }

}
