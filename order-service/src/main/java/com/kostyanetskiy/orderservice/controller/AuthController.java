package com.kostyanetskiy.orderservice.controller;

import com.kostyanetskiy.orderservice.dto.AuthRequest;
import com.kostyanetskiy.orderservice.security.JwtUtil;
import com.kostyanetskiy.orderservice.dto.UserRequest;
import com.kostyanetskiy.orderservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(@RequestBody UserRequest userRequest) {
        userService.createUser(userRequest);
        String token = jwtUtil.generateToken(userRequest.getUsername());
        return Map.of("jwt-token", token);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> login(@RequestBody AuthRequest authRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            return Map.of("message", "Invalid credentials " + e.getMessage());
        }

        String token = jwtUtil.generateToken(authRequest.getUsername());
        return Map.of("jwt-token", token);
    }

}
