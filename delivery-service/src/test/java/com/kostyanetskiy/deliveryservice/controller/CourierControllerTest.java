package com.kostyanetskiy.deliveryservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kostyanetskiy.deliveryservice.dto.CourierCreateRequest;
import com.kostyanetskiy.deliveryservice.dto.CourierResponse;
import com.kostyanetskiy.deliveryservice.enums.CourierStatus;
import com.kostyanetskiy.deliveryservice.service.CourierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class CourierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourierService courierService;

    @Test
    void getAll() throws Exception {
        List<CourierResponse> responseList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            CourierResponse courierResponse = CourierResponse.builder()
                    .name("Courier" + i)
                    .code("COUR01" + i)
                    .status(CourierStatus.FREE.name())
                    .deliveries(Collections.emptyList())
                    .build();
            responseList.add(courierResponse);
        }

        when(courierService.allCouriers()).thenReturn(responseList);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/courier/all")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[3].code", is(responseList.get(3).getCode())))
                .andExpect(jsonPath("$[3].name", is(responseList.get(3).getName())));
    }

    @Test
    void create() throws Exception {
        CourierCreateRequest courierCreateRequest = CourierCreateRequest.builder()
                .name("Alex")
                .password("a$l$x")
                .build();

        CourierResponse courierResponse = CourierResponse.builder()
                .name("Alex")
                .code("CODEALEX1")
                .status(CourierStatus.FREE.name())
                .deliveries(Collections.emptyList())
                .build();

        when(courierService.createCourier(courierCreateRequest)).thenReturn(courierResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/courier/create")
                .content(new ObjectMapper().writeValueAsString(courierCreateRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.name", is(courierResponse.getName())))
                .andExpect(jsonPath("$.code", is(courierResponse.getCode())))
                .andExpect(jsonPath("$.status", is(courierResponse.getStatus())));
    }
}
