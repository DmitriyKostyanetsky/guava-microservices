package com.kostyanetskiy.orderservice.service.impl;

import com.kostyanetskiy.orderservice.dto.*;
import com.kostyanetskiy.orderservice.enums.OrderStatus;
import com.kostyanetskiy.orderservice.event.OrderPlaceEvent;
import com.kostyanetskiy.orderservice.event.OrderReceiveEvent;
import com.kostyanetskiy.orderservice.exception.OrderNotFoundException;
import com.kostyanetskiy.orderservice.exception.OrderOnDeliveryException;
import com.kostyanetskiy.orderservice.exception.UnrecognizedOrderException;
import com.kostyanetskiy.orderservice.model.Order;
import com.kostyanetskiy.orderservice.repository.OrderRepository;
import com.kostyanetskiy.orderservice.security.PersonDetails;
import com.kostyanetskiy.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final KafkaTemplate<String, OrderPlaceEvent> kafkaTemplate;
    private final WebClient.Builder webClientBuilder;

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
        order.setTrackNo("");

        PersonDetails principal = getPrincipal();
        order.setUser(principal.getUser());
        orderRepository.save(order);
        kafkaTemplate.send("orderSend1", new OrderPlaceEvent(
                order.getCode(),
                order.getReceiver(),
                order.getAddress(),
                order.getItemName()
        ));

        return createResponse(order);
    }

    @KafkaListener(topics = "orderReceive3",
            properties = {"spring.json.value.default.type=com.kostyanetskiy.orderservice.event.OrderReceiveEvent"})
    @Override
    public void receiveOrder(OrderReceiveEvent orderReceiveEvent) {
        Optional<Order> optionalOrder = orderRepository.findByCode(orderReceiveEvent.getOrderCode());
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(mapStatus(orderReceiveEvent.getStatus()));
            order.setTrackNo(orderReceiveEvent.getTrackNo());
            orderRepository.save(order);
        }
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
            Order order = orderOptional.get();
            order.setStatus(OrderStatus.CANCEL);
            orderRepository.save(order);
        }
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = getUserOrders();
        return orders.stream()
                .map(this::createResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeliveryResponse getOrderByTrackNo(String trackNo) {
        DeliveryResponse deliveryResponse = webClientBuilder.build().get()
                .uri("http://delivery-service/api/v1/delivery/track",
                        uriBuilder -> uriBuilder.queryParam("trackNo", trackNo).build())
                .retrieve()
                .bodyToMono(DeliveryResponse.class)
                .block();

        return DeliveryResponse.builder()
                .courierName(deliveryResponse.getCourierName())
                .deliveryCode(deliveryResponse.getDeliveryCode())
                .orderCode(deliveryResponse.getOrderCode())
                .status(deliveryResponse.getStatus())
                .build();
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
                .status(order.getStatus().name())
                .trackNo(order.getTrackNo())
                .build();
    }

    private PersonDetails getPrincipal() {
        return (PersonDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private OrderStatus mapStatus(String deliveryStatus) {
        switch (deliveryStatus) {
            case "CREATED":
                return OrderStatus.CREATED;
            case "ON_DELIVERY":
                return OrderStatus.IN_WORK;
            case "CANCEL":
                return OrderStatus.CANCEL;
            case "FINISH":
                return OrderStatus.FINISH;
        }

        throw new UnrecognizedOrderException("Order status not found!");
    }

}
