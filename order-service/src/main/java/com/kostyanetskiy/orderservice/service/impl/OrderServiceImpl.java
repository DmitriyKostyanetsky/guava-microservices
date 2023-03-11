package com.kostyanetskiy.orderservice.service.impl;

import com.kostyanetskiy.orderservice.dto.OrderRequestCreate;
import com.kostyanetskiy.orderservice.dto.OrderRequest;
import com.kostyanetskiy.orderservice.dto.OrderRequestChange;
import com.kostyanetskiy.orderservice.dto.OrderResponse;
import com.kostyanetskiy.orderservice.enums.OrderStatus;
import com.kostyanetskiy.orderservice.exception.OrderNotFoundException;
import com.kostyanetskiy.orderservice.exception.OrderOnDeliveryException;
import com.kostyanetskiy.orderservice.model.Order;
import com.kostyanetskiy.orderservice.repository.OrderRepository;
import com.kostyanetskiy.orderservice.security.PersonDetails;
import com.kostyanetskiy.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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

        PersonDetails principal = getPrincipal();
        order.setUser(principal.getUser());
        orderRepository.save(order);


        //TODO send order to delivery service (kafka) el

        return createResponse(order);
    }

    @Override
    public OrderResponse changeOrder(OrderRequestChange orderRequestChange) {
        List<Order> orders = getUserOrders();

        Optional<Order> orderOptional = orders.stream()
                .filter(order -> order.getCode().equals(orderRequestChange.getCode()))
                .findFirst();

        if (orderOptional.isEmpty())
            throw new OrderNotFoundException("Order not found");

        Order order = orderOptional.get();
        if (order.getStatus().equals(OrderStatus.CREATED)) {
            order.setReceiver(orderRequestChange.getReceiver());
            order.setAddress(orderRequestChange.getAddress());
            orderRepository.save(order);

            return createResponse(order);
        } else {
            throw new OrderOnDeliveryException("Your order status is " + order.getStatus() + ". You cannot change it");
        }
    }

    @Override
    public void cancelOrder(OrderRequest orderRequest) {
        List<Order> orders = getUserOrders();

        Optional<Order> orderOptional = orders.stream()
                .filter(order -> order.getCode().equals(orderRequest.getCode()))
                .findFirst();

        if (orderOptional.isPresent()) {
            orderOptional.ifPresent(orderRepository::delete);
        }
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = getUserOrders();
        return orders.stream()
                .map(this::createResponse)
                .collect(Collectors.toList());
    }

    private List<Order> getUserOrders() {
        PersonDetails principal = getPrincipal();
        return orderRepository.findByUserId(principal.getUser().getId());
    }

    private OrderResponse createResponse(Order order) {
        return OrderResponse.builder()
                .address(order.getAddress())
                .code(order.getCode())
                .receiver(order.getReceiver())
                .createdDate(order.getCreatedDate())
                .itemName(order.getItemName())
                .build();
    }

    private PersonDetails getPrincipal() {
        return (PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
