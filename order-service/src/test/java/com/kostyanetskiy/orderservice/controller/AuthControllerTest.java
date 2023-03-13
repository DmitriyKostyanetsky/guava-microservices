package com.kostyanetskiy.orderservice.controller;

import com.kostyanetskiy.orderservice.config.SecurityConfig;
import com.kostyanetskiy.orderservice.security.JwtFilter;
import com.kostyanetskiy.orderservice.security.JwtUtil;
import com.kostyanetskiy.orderservice.service.UserService;
import com.kostyanetskiy.orderservice.service.impl.PersonDetailsService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PersonDetailsService personDetailsService;


    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    void registration() throws Exception {

//        when(userService.createUser()).thenReturn(responseList);

        Map<String, String> map = Map.of("jwt-token", "iamjwttoken");

        when(jwtUtil.generateToken("username")).thenReturn("iamjwttoken");

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/auth/registration")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(200));
//                .andExpect(jsonPath("$[3].name", is(map.get(""))))
//                .andExpect(jsonPath("$[3].id", is(responseList.get(3).getId())));
    }

}
