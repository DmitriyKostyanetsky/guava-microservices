package com.kostyanetskiy.orderservice.db;

import com.kostyanetskiy.orderservice.OrderServiceApplication;
import com.kostyanetskiy.orderservice.config.H2TestProfileJPAConfig;
import com.kostyanetskiy.orderservice.enums.OrderStatus;
import com.kostyanetskiy.orderservice.model.Order;
import com.kostyanetskiy.orderservice.model.User;
import com.kostyanetskiy.orderservice.repository.OrderRepository;
import com.kostyanetskiy.orderservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        OrderServiceApplication.class,
        H2TestProfileJPAConfig.class})
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private Order order;
    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setUsername("test");
        user.setRole("ROLE_USER");
        user.setPhone("8765432");
        user.setEmail("email@email.com");

        order = new Order();
        order.setStatus(OrderStatus.IN_WORK);
        order.setUser(user);
        order.setItemName("samsung1");
        order.setReceiver("Ivanov Ivan");
        order.setCode("123");

        user.setOrders(List.of(order));

        userRepository.save(user);
        orderRepository.save(order);
    }

    @Test
    void findByCode() {
        Optional<Order> optionalOrder = orderRepository.findByCode("123");
        assertTrue(optionalOrder.isPresent());
        Order order = optionalOrder.get();
        assertEquals(this.order, order);
    }

    @Test
    void findByUserId() {
        User userWithId = userRepository.save(this.user);
        List<Order> orders = orderRepository.findByUserId(userWithId.getId());
        assertFalse(orders.isEmpty());
        Order order = orders.get(0);
        assertEquals(this.order, order);
    }

}
