package com.kostyanetskiy.orderservice.controller;

import com.kostyanetskiy.orderservice.dto.UserRequest;
import com.kostyanetskiy.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String register(@RequestBody UserRequest userRequest) {
        return userService.createUser(userRequest);
    }

}
