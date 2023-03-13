package com.kostyanetskiy.deliveryservice.controller;

import com.kostyanetskiy.deliveryservice.dto.DeliveryAssignRequest;
import com.kostyanetskiy.deliveryservice.dto.DeliveryChangeRequest;
import com.kostyanetskiy.deliveryservice.dto.DeliveryResponse;
import com.kostyanetskiy.deliveryservice.service.DeliveryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/delivery")
@SecurityRequirement(name = "basicAuth")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PutMapping("/change")
    @ResponseStatus(HttpStatus.OK)
    public DeliveryResponse changeDelivery(@RequestBody DeliveryChangeRequest deliveryChangeRequest) {
        return deliveryService.change(deliveryChangeRequest);
    }

    @PutMapping("/assign")
    @ResponseStatus(HttpStatus.OK)
    public DeliveryResponse assignDelivery(@RequestBody DeliveryAssignRequest deliveryRequest) {
        return deliveryService.assignOnCourier(deliveryRequest);
    }

    @GetMapping("/track")
    @ResponseStatus(HttpStatus.OK)
    public DeliveryResponse getByTrack(@RequestParam String trackNo) {
        return deliveryService.details(trackNo);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<DeliveryResponse> getAll() {
        return deliveryService.getAll();
    }

    @GetMapping("/allByCourier")
    @ResponseStatus(HttpStatus.OK)
    public List<DeliveryResponse> getAllByCourier(@RequestParam String courierCode) {
        return deliveryService.allDeliveriesByCourier(courierCode);
    }
}
