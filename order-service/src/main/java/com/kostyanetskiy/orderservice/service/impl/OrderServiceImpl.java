package com.kostyanetskiy.orderservice.service.impl;

import com.kostyanetskiy.orderservice.dto.OrderRequestCreate;
import com.kostyanetskiy.orderservice.dto.OrderRequest;
import com.kostyanetskiy.orderservice.dto.OrderRequestChange;
import com.kostyanetskiy.orderservice.dto.OrderResponse;
import com.kostyanetskiy.orderservice.enums.OrderStatus;
import com.kostyanetskiy.orderservice.model.Order;
import com.kostyanetskiy.orderservice.model.User;
import com.kostyanetskiy.orderservice.repository.OrderRepository;
import com.kostyanetskiy.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public OrderResponse createOrder(OrderRequestCreate orderRequestCreate) {
        String code = UUID.randomUUID().toString();
        Date date = new Date(System.currentTimeMillis());
        Order order = new Order();
        order.setAddress(orderRequestCreate.getAddress());
        order.setReceiver(orderRequestCreate.getReceiver());
        order.setItemName(orderRequestCreate.getItemName());
        order.setCode(code);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedDate(date);
        //TODO user get from context
        order.setUser(new User());
        orderRepository.save(order);


        //TODO send order to delivery service (kafka) el

        return createResponse(order);
    }

    @Override
    public OrderResponse changeOrder(OrderRequestChange orderRequestChange) {
        Order order = orderRepository.findByCode(orderRequestChange.getCode()).orElseThrow();
        if (order.getStatus().equals(OrderStatus.CREATED)) {
            order.setReceiver(orderRequestChange.getReceiver());
            order.setAddress(orderRequestChange.getAddress());
            orderRepository.save(order);

            return createResponse(order);
        }

        return null;
    }

    @Override
    public void cancelOrder(OrderRequest orderRequest) {
        Optional<Order> optionalOrder = orderRepository.findByCode(orderRequest.getCode());
        optionalOrder.ifPresent(orderRepository::delete);
    }

    @Override
    public List<OrderResponse> getAllOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(this::createResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse createResponse(Order order) {
        return OrderResponse.builder()
                .address(order.getAddress())
                .code(order.getCode())
                .receiver(order.getReceiver())
                .createdDate(order.getCreatedDate())
                .build();
    }
}
