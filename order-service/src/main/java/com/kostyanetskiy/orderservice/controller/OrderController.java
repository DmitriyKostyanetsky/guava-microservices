package com.kostyanetskiy.orderservice.controller;

import com.kostyanetskiy.orderservice.dto.*;
import com.kostyanetskiy.orderservice.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class)) })})
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@RequestBody OrderRequestCreate orderRequestCreate) {
        return orderService.createOrder(orderRequestCreate);
    }

    @Operation(summary = "Change order receiver or order address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order details success change",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content) })
    @PutMapping("/change")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse changeOrder(@RequestBody OrderRequestChange orderRequest) {
        return orderService.changeOrder(orderRequest);
    }

    @Operation(summary = "Cancel order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order was canceled",
                    content = @Content) })
    @DeleteMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOrder(@RequestBody OrderRequest orderRequest) {
        orderService.cancelOrder(orderRequest);
    }

    @Operation(summary = "Get all user orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders was received",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OrderResponse.class)) })})
    @GetMapping(path = "/all")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrder() {
        return orderService.getAllOrders();
    }

    @Operation(summary = "Track order by tracking number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return found order",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content) })
    @GetMapping(path = "/track")
    @ResponseStatus(HttpStatus.OK)
    public DeliveryResponse getOrderByTrackNo(@RequestParam String trackNo) {
        return orderService.getOrderByTrackNo(trackNo);
    }
}
