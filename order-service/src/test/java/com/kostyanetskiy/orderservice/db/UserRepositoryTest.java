package com.kostyanetskiy.orderservice.db;

import com.kostyanetskiy.orderservice.OrderServiceApplication;
import com.kostyanetskiy.orderservice.config.H2TestProfileJPAConfig;
import com.kostyanetskiy.orderservice.model.User;
import com.kostyanetskiy.orderservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
        OrderServiceApplication.class,
        H2TestProfileJPAConfig.class})
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setRole("ROLE_USER");
        user.setPassword("124");
        user.setUsername("Dmitriy");
        user.setPhone("8765432");
        user.setEmail("email@email.com");
        user.setOrders(Collections.emptyList());
    }

    @Test
    void findUserByName() {
        userRepository.save(user);
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        assertTrue(optionalUser.isPresent());
        assertEquals(user, optionalUser.get());
    }
}
