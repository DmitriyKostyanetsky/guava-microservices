package com.kostyanetskiy.orderservice.service;

import com.kostyanetskiy.orderservice.dto.UserRequest;

public interface UserService {
    void createUser(UserRequest userRequest);
}
