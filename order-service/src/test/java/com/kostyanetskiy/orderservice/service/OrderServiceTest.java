package com.kostyanetskiy.orderservice.service;

import com.kostyanetskiy.orderservice.dto.*;
import com.kostyanetskiy.orderservice.enums.OrderStatus;
import com.kostyanetskiy.orderservice.event.OrderPlaceEvent;
import com.kostyanetskiy.orderservice.event.OrderReceiveEvent;
import com.kostyanetskiy.orderservice.model.Order;
import com.kostyanetskiy.orderservice.model.User;
import com.kostyanetskiy.orderservice.repository.OrderRepository;
import com.kostyanetskiy.orderservice.repository.UserRepository;
import com.kostyanetskiy.orderservice.security.PersonDetails;
import com.kostyanetskiy.orderservice.service.impl.OrderServiceImpl;
import com.kostyanetskiy.orderservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = OrderServiceImpl.class)
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private KafkaTemplate<String, OrderPlaceEvent> kafkaTemplate;

    @MockBean
    private WebClient.Builder webClientBuilder;


    @Test
    void createOrder() {
        OrderRequestCreate orderRequestCreate = OrderRequestCreate.builder()
                .address("address")
                .itemName("table")
                .receiver("Mostovoy Artem")
                .build();

        Order order = new Order();
        order.setAddress(orderRequestCreate.getAddress());
        order.setReceiver(orderRequestCreate.getReceiver());
        order.setItemName(orderRequestCreate.getItemName());
        order.setCode("");
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedDate(new Date(System.currentTimeMillis()));
        order.setTrackNo("");

        securityMock();
        when(orderRepository.save(new Order())).thenReturn(order);

        OrderResponse orderResponse = orderService.createOrder(orderRequestCreate);

        assertEquals(orderRequestCreate.getAddress(), orderResponse.getAddress());
        assertEquals(orderRequestCreate.getItemName(), orderResponse.getItemName());
        assertEquals(OrderStatus.CREATED.name(), orderResponse.getStatus());
        assertEquals("", orderResponse.getTrackNo());
        assertNotNull(orderResponse.getCode());
        assertNotNull(orderResponse.getCreatedDate());
    }

    @Test
    void cancelOrder() {
        OrderRequest orderRequest = OrderRequest.builder()
                .code("order-123-afa-gs")
                .build();

        User user = getUser();
        Order order = getOrder(user, "order-123-afa-gs");

        securityMock();
        when(orderRepository.findByUserId(anyLong())).thenReturn(List.of(order));

        orderService.cancelOrder(orderRequest);
    }

    @Test
    void getAllOrders() {
        User user = getUser();
        Order order = getOrder(user, "order-123-afa-gs");
        Order order1 = getOrder(user, order);

        securityMock();
        when(orderRepository.findByUserId(anyLong())).thenReturn(List.of(order, order1));

        List<OrderResponse> orders = orderService.getAllOrders();

        assertEquals(2, orders.size());
        assertEquals("order-123-afa-gs", orders.get(0).getCode());
        assertEquals("order-124-ygg-evm", orders.get(1).getCode());
    }

    @Test
    void changeOrder() {
        OrderRequestChange orderRequestChange = OrderRequestChange.builder()
                .address("NY street 11")
                .code("CODE123")
                .receiver("Philipp")
                .build();

        User user = getUser();
        Order order = getOrder(user, "CODE123");
        Order order1 = getOrder(user, order);

        securityMock();
        when(orderRepository.findByUserId(anyLong())).thenReturn(List.of(order, order1));

        OrderResponse orderResponse = orderService.changeOrder(orderRequestChange);

        assertEquals(orderRequestChange.getAddress(), orderResponse.getAddress());
        assertEquals(orderRequestChange.getReceiver(), orderResponse.getReceiver());
        assertEquals(orderRequestChange.getCode(), orderResponse.getCode());
    }

    @Test
    void receiveOrder() {
        String code = "CODE!12";
        OrderReceiveEvent orderReceiveEvent = new OrderReceiveEvent();
        orderReceiveEvent.setOrderCode("Alex");
        orderReceiveEvent.setOrderCode(code);
        orderReceiveEvent.setStatus(OrderStatus.CREATED.name());
        orderReceiveEvent.setTrackNo("DLVR0101010101");

        User user = getUser();
        Order order = getOrder(user, code);

        when(orderRepository.findByCode(code)).thenReturn(Optional.of(order));

        orderService.receiveOrder(orderReceiveEvent);
    }

    private User getUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("pass");
        user.setRole("ROLE_USER");
        user.setId(1L);
        return user;
    }

    private Order getOrder(User user, Order order) {
        Order order1 = new Order();
        order1.setAddress("address 2");
        order1.setReceiver("Anna");
        order1.setItemName("table");
        order1.setCode("order-124-ygg-evm");
        order1.setStatus(OrderStatus.FINISH);
        order1.setCreatedDate(new Date(System.currentTimeMillis()));
        order1.setTrackNo("DLVR9423");
        order.setUser(user);
        return order1;
    }

    private Order getOrder(User user, String code) {
        Order order = new Order();
        order.setAddress("address");
        order.setReceiver("Mike");
        order.setItemName("chair");
        order.setCode(code);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedDate(new Date(System.currentTimeMillis()));
        order.setTrackNo("");
        order.setUser(user);
        return order;
    }

    private void securityMock() {
        User user = mock(User.class);
        PersonDetails personDetails = mock(PersonDetails.class);
        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(personDetails);
        when(personDetails.getUser()).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        SecurityContextHolder.setContext(securityContext);
    }
}
