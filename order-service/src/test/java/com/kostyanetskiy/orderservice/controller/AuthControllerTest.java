package com.kostyanetskiy.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kostyanetskiy.orderservice.dto.AuthRequest;
import com.kostyanetskiy.orderservice.dto.UserRequest;
import com.kostyanetskiy.orderservice.security.JwtUtil;
import com.kostyanetskiy.orderservice.service.UserService;
import com.kostyanetskiy.orderservice.service.impl.PersonDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PersonDetailsService personDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    private final String token = "iamjwttoken";

    @Test
    void registration() throws Exception {
        UserRequest userRequest = UserRequest.builder()
                .username("test username")
                .phone("9098980")
                .password("test password")
                .email("email@email")
                .build();

        when(jwtUtil.generateToken(userRequest.getUsername())).thenReturn(token);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/auth/registration")
                .content(new ObjectMapper().writeValueAsString(userRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(jsonPath("jwt-token", is(token)));
    }

    @Test
    void login() throws Exception {
        AuthRequest authRequest = AuthRequest.builder()
                .username("username")
                .password("pass")
                .build();

        when(jwtUtil.generateToken(authRequest.getUsername())).thenReturn(token);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/auth/login")
                .content(new ObjectMapper().writeValueAsString(authRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("jwt-token", is(token)));
    }

}
