package com.kostyanetskiy.orderservice.service;

import com.kostyanetskiy.orderservice.dto.OrderRequestCreate;
import com.kostyanetskiy.orderservice.dto.OrderRequest;
import com.kostyanetskiy.orderservice.dto.OrderRequestChange;
import com.kostyanetskiy.orderservice.dto.OrderResponse;
import com.kostyanetskiy.orderservice.event.OrderReceiveEvent;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequestCreate orderRequestCreate);
    void receiveOrder(OrderReceiveEvent orderReceiveEvent);
    OrderResponse changeOrder(OrderRequestChange orderRequestChange);
    void cancelOrder(OrderRequest orderRequest);
    List<OrderResponse> getAllOrders();
}
