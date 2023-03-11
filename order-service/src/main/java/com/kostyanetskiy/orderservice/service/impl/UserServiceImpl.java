package com.kostyanetskiy.orderservice.service.impl;

import com.kostyanetskiy.orderservice.dto.UserRequest;
import com.kostyanetskiy.orderservice.model.User;
import com.kostyanetskiy.orderservice.repository.UserRepository;
import com.kostyanetskiy.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public String createUser(UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        userRepository.save(user);

        //JTW create
        return UUID.randomUUID().toString();
    }


}
