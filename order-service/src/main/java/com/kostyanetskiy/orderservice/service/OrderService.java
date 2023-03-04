package com.kostyanetskiy.orderservice.service;

import com.kostyanetskiy.orderservice.dto.OrderRequestCreate;
import com.kostyanetskiy.orderservice.dto.OrderRequest;
import com.kostyanetskiy.orderservice.dto.OrderRequestChange;
import com.kostyanetskiy.orderservice.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequestCreate orderRequestCreate);
    OrderResponse changeOrder(OrderRequestChange orderRequestChange);
    void cancelOrder(OrderRequest orderRequest);
    List<OrderResponse> getAllOrders(Long userId);
}
