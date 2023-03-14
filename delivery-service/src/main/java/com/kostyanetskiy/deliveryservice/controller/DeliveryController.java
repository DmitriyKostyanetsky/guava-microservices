package com.kostyanetskiy.deliveryservice.controller;

import com.kostyanetskiy.deliveryservice.dto.DeliveryAssignRequest;
import com.kostyanetskiy.deliveryservice.dto.DeliveryChangeRequest;
import com.kostyanetskiy.deliveryservice.dto.DeliveryResponse;
import com.kostyanetskiy.deliveryservice.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Change delivery status using tracking number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delivery status was updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Not found delivery by tracking number",
                    content = @Content) })
    @PutMapping("/change")
    @ResponseStatus(HttpStatus.OK)
    public DeliveryResponse changeDelivery(@RequestBody DeliveryChangeRequest deliveryChangeRequest) {
        return deliveryService.change(deliveryChangeRequest);
    }

    @Operation(summary = "Assign delivery on courier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delivery was assigned",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Courier or delivery not found",
                    content = @Content) })
    @PutMapping("/assign")
    @ResponseStatus(HttpStatus.OK)
    public DeliveryResponse assignDelivery(@RequestBody DeliveryAssignRequest deliveryRequest) {
        return deliveryService.assignOnCourier(deliveryRequest);
    }

    @Operation(summary = "Track delivery by tracking number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return delivery details",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Delivery not found",
                    content = @Content) })
    @GetMapping("/track")
    @ResponseStatus(HttpStatus.OK)
    public DeliveryResponse getByTrack(@RequestParam String trackNo) {
        return deliveryService.details(trackNo);
    }

    @Operation(summary = "Get all deliveries")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return deliveries",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryResponse.class)) })})
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<DeliveryResponse> getAll() {
        return deliveryService.getAll();
    }

    @Operation(summary = "Get all deliveries by courier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return deliveries by courier",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Courier not found",
                    content = @Content) })
    @GetMapping("/allByCourier")
    @ResponseStatus(HttpStatus.OK)
    public List<DeliveryResponse> getAllByCourier(@RequestParam String courierCode) {
        return deliveryService.allDeliveriesByCourier(courierCode);
    }
}
