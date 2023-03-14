package com.kostyanetskiy.orderservice.controller;

import com.kostyanetskiy.orderservice.dto.AuthRequest;
import com.kostyanetskiy.orderservice.model.User;
import com.kostyanetskiy.orderservice.security.JwtUtil;
import com.kostyanetskiy.orderservice.dto.UserRequest;
import com.kostyanetskiy.orderservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "New user registration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully register new user",
                    content = @Content) })
    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> register(@RequestBody UserRequest userRequest) {
        userService.createUser(userRequest);
        String token = jwtUtil.generateToken(userRequest.getUsername());
        return Map.of("jwt-token", token);
    }

    @Operation(summary = "Refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Return new token",
                    content = @Content) })
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
