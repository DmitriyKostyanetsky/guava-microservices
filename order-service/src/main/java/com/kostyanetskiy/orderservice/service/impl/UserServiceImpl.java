package com.kostyanetskiy.orderservice.service.impl;

import com.kostyanetskiy.orderservice.dto.UserRequest;
import com.kostyanetskiy.orderservice.model.User;
import com.kostyanetskiy.orderservice.repository.UserRepository;
import com.kostyanetskiy.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createUser(UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setRole("ROLE_USER");
        userRepository.save(user);
    }


}
