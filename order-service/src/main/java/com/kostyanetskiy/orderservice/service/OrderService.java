package com.kostyanetskiy.orderservice.service;

import com.kostyanetskiy.orderservice.dto.*;
import com.kostyanetskiy.orderservice.event.OrderReceiveEvent;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequestCreate orderRequestCreate);
    void receiveOrder(OrderReceiveEvent orderReceiveEvent);
    OrderResponse changeOrder(OrderRequestChange orderRequestChange);
    void cancelOrder(OrderRequest orderRequest);
    List<OrderResponse> getAllOrders();
    DeliveryResponse getOrderByTrackNo(String trackNo);
}
