package com.kostyanetskiy.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kostyanetskiy.orderservice.dto.*;
import com.kostyanetskiy.orderservice.enums.OrderStatus;
import com.kostyanetskiy.orderservice.security.JwtUtil;
import com.kostyanetskiy.orderservice.service.OrderService;
import com.kostyanetskiy.orderservice.service.impl.PersonDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private PersonDetailsService personDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void getAll() throws Exception {
        List<OrderResponse> responseList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            OrderResponse orderResponse = OrderResponse.builder()
                    .trackNo("DLVR" + i)
                    .status(OrderStatus.CREATED.name())
                    .itemName("iphone" + i)
                    .createdDate(new Date(System.currentTimeMillis()))
                    .receiver("Anna")
                    .code("CODE" + i)
                    .address("test address street" + i)
                    .build();
            responseList.add(orderResponse);
        }

        when(orderService.getAllOrders()).thenReturn(responseList);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/order/all")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[3].code", is(responseList.get(3).getCode())))
                .andExpect(jsonPath("$[3].trackNo", is(responseList.get(3).getTrackNo())));
    }

    @Test
    void create() throws Exception {
        OrderResponse orderResponse = OrderResponse.builder()
                .address("NY street 12")
                .code("CODE123")
                .receiver("Mike")
                .createdDate(new Date(System.currentTimeMillis()))
                .itemName("Laptop")
                .status(OrderStatus.CREATED.name())
                .trackNo("")
                .build();

        OrderRequestCreate requestCreate = OrderRequestCreate.builder()
                .receiver("Mike")
                .itemName("Laptop")
                .address("NY street 12")
                .build();

        when(orderService.createOrder(requestCreate)).thenReturn(orderResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/order/create")
                .content(new ObjectMapper().writeValueAsString(requestCreate))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.code", is(orderResponse.getCode())))
                .andExpect(jsonPath("$.trackNo", is(orderResponse.getTrackNo())))
                .andExpect(jsonPath("$.address", is(orderResponse.getAddress())))
                .andExpect(jsonPath("$.createdDate", is(orderResponse.getCreatedDate().toString())))
                .andExpect(jsonPath("$.itemName", is(orderResponse.getItemName())))
                .andExpect(jsonPath("$.receiver", is(orderResponse.getReceiver())))
                .andExpect(jsonPath("$.status", is(orderResponse.getStatus())));
    }

    @Test
    void changeOrder() throws Exception {
        OrderResponse orderResponse = OrderResponse.builder()
                .address("NY street 13")
                .code("CODE123")
                .receiver("Alex")
                .createdDate(new Date(System.currentTimeMillis()))
                .itemName("Laptop")
                .status(OrderStatus.CREATED.name())
                .trackNo("")
                .build();

        OrderRequestChange requestChange = OrderRequestChange.builder()
                .receiver("Alex")
                .address("NY street 13")
                .build();

        when(orderService.changeOrder(requestChange)).thenReturn(orderResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/order/change")
                .content(new ObjectMapper().writeValueAsString(requestChange))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.code", is(orderResponse.getCode())))
                .andExpect(jsonPath("$.trackNo", is(orderResponse.getTrackNo())))
                .andExpect(jsonPath("$.address", is(orderResponse.getAddress())))
                .andExpect(jsonPath("$.createdDate", is(orderResponse.getCreatedDate().toString())))
                .andExpect(jsonPath("$.itemName", is(orderResponse.getItemName())))
                .andExpect(jsonPath("$.receiver", is(orderResponse.getReceiver())))
                .andExpect(jsonPath("$.status", is(orderResponse.getStatus())));
    }

    @Test
    void track() throws Exception {
        String trackNo = "DLVR1012";

        DeliveryResponse deliveryResponse = DeliveryResponse.builder()
                .deliveryCode(trackNo)
                .courierName("Maxim")
                .orderCode("CODE124")
                .status(OrderStatus.IN_WORK.name())
                .build();

        when(orderService.getOrderByTrackNo(trackNo)).thenReturn(deliveryResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/order/track")
                .param("trackNo", trackNo)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.orderCode", is(deliveryResponse.getOrderCode())))
                .andExpect(jsonPath("$.deliveryCode", is(deliveryResponse.getDeliveryCode())))
                .andExpect(jsonPath("$.courierName", is(deliveryResponse.getCourierName())))
                .andExpect(jsonPath("$.status", is(deliveryResponse.getStatus())));
    }

    @Test
    void cancelOrder() throws Exception {
        OrderRequest orderRequest = OrderRequest.builder()
                .code("CODE1234567")
                .build();

        doNothing().when(orderService).cancelOrder(orderRequest);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/order/remove")
                .content(new ObjectMapper().writeValueAsString(orderRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(200));
    }

}
