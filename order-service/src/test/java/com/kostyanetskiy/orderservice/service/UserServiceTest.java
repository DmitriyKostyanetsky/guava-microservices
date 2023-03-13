package com.kostyanetskiy.orderservice.service;

import com.kostyanetskiy.orderservice.dto.UserRequest;
import com.kostyanetskiy.orderservice.model.User;
import com.kostyanetskiy.orderservice.repository.UserRepository;
import com.kostyanetskiy.orderservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserServiceImpl.class)
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void createUser() {
        UserRequest userRequest = UserRequest.builder()
                .email("email")
                .password("123")
                .phone("909090909")
                .username("testuser")
                .build();

        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setRole("ROLE_USER");

        when(userRepository.save(user)).thenReturn(user);

        userService.createUser(userRequest);
    }

}
