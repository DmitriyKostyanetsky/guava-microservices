package com.kostyanetskiy.orderservice.controller;

import com.kostyanetskiy.orderservice.dto.*;
import com.kostyanetskiy.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse placeOrder(@RequestBody OrderRequestCreate orderRequestCreate) {
        return orderService.createOrder(orderRequestCreate);
    }

    @PutMapping("/change")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse changeOrder(@RequestBody OrderRequestChange orderRequest) {
        return orderService.changeOrder(orderRequest);
    }

    @DeleteMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public void deleteOrder(@RequestBody OrderRequest orderRequest) {
        orderService.cancelOrder(orderRequest);
    }

    @GetMapping(path = "/all")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getOrder() {
        return orderService.getAllOrders();
    }

    @GetMapping(path = "/track")
    @ResponseStatus(HttpStatus.OK)
    public DeliveryResponse getOrderByTrackNo(@RequestParam String trackNo) {
        return orderService.getOrderByTrackNo(trackNo);
    }
}
