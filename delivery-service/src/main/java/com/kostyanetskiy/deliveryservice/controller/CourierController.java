package com.kostyanetskiy.deliveryservice.controller;

import com.kostyanetskiy.deliveryservice.dto.CourierCreateRequest;
import com.kostyanetskiy.deliveryservice.dto.CourierResponse;
import com.kostyanetskiy.deliveryservice.service.CourierService;
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
@RequestMapping("/api/v1/courier")
@SecurityRequirement(name = "basicAuth")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    @Operation(summary = "Create new courier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Courier was added",
                    content = { @Content(mediaType = "application/json")})})
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public CourierResponse create(@RequestBody CourierCreateRequest courierCreateRequest) {
        return courierService.createCourier(courierCreateRequest);
    }

    @Operation(summary = "Get all couriers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All couriers",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CourierResponse.class)) })})
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<CourierResponse> allCouriers() {
        return courierService.allCouriers();
    }

}
