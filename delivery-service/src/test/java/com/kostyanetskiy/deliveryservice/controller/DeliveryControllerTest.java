package com.kostyanetskiy.deliveryservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kostyanetskiy.deliveryservice.dto.DeliveryAssignRequest;
import com.kostyanetskiy.deliveryservice.dto.DeliveryChangeRequest;
import com.kostyanetskiy.deliveryservice.dto.DeliveryResponse;
import com.kostyanetskiy.deliveryservice.enums.DeliveryStatus;
import com.kostyanetskiy.deliveryservice.service.DeliveryService;
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
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService deliveryService;

    @Test
    void getAll() throws Exception {
        List<DeliveryResponse> responseList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DeliveryResponse deliveryResponse = DeliveryResponse.builder()
                    .orderCode("ORDER1" + i)
                    .deliveryCode("DLVR" + i)
                    .courierName("Alex")
                    .status(DeliveryStatus.ON_DELIVERY.name())
                    .build();
            responseList.add(deliveryResponse);
        }

        when(deliveryService.getAll()).thenReturn(responseList);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/delivery/all")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[3].orderCode", is(responseList.get(3).getOrderCode())))
                .andExpect(jsonPath("$[3].status", is(responseList.get(3).getStatus())))
                .andExpect(jsonPath("$[3].courierName", is(responseList.get(3).getCourierName())))
                .andExpect(jsonPath("$[3].deliveryCode", is(responseList.get(3).getDeliveryCode())));
    }

    @Test
    void allDeliveriesByCourier() throws Exception {
        String courierCode = "COUR1";
        List<DeliveryResponse> responseList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            DeliveryResponse deliveryResponse = DeliveryResponse.builder()
                    .orderCode("ORDER1" + i)
                    .deliveryCode("DLVR" + i)
                    .courierName("Alex")
                    .status(DeliveryStatus.ON_DELIVERY.name())
                    .build();
            responseList.add(deliveryResponse);
        }

        when(deliveryService.allDeliveriesByCourier(courierCode)).thenReturn(responseList);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/delivery/allByCourier")
                .param("courierCode", courierCode)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$[1].orderCode", is(responseList.get(1).getOrderCode())))
                .andExpect(jsonPath("$[2].status", is(responseList.get(2).getStatus())))
                .andExpect(jsonPath("$[3].courierName", is(responseList.get(3).getCourierName())))
                .andExpect(jsonPath("$[4].deliveryCode", is(responseList.get(4).getDeliveryCode())));
    }

    @Test
    void track() throws Exception {
        String trackNo = "DLVR1012";

        DeliveryResponse deliveryResponse = DeliveryResponse.builder()
                .deliveryCode(trackNo)
                .courierName("Maxim")
                .orderCode("CODE124")
                .status(DeliveryStatus.ON_DELIVERY.name())
                .build();

        when(deliveryService.details(trackNo)).thenReturn(deliveryResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/delivery/track")
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
    void changeOrder() throws Exception {
        String trackNo = "DLVR!01";

        DeliveryChangeRequest deliveryChangeRequest = DeliveryChangeRequest.builder()
                .status(DeliveryStatus.CANCEL)
                .trackNo(trackNo)
                .build();

        DeliveryResponse deliveryResponse = DeliveryResponse.builder()
                .deliveryCode(trackNo)
                .courierName("Maxim")
                .orderCode("CODE124")
                .status(DeliveryStatus.ON_DELIVERY.name())
                .build();

        when(deliveryService.change(deliveryChangeRequest)).thenReturn(deliveryResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/delivery/change")
                .content(new ObjectMapper().writeValueAsString(deliveryChangeRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.deliveryCode", is(deliveryResponse.getDeliveryCode())))
                .andExpect(jsonPath("$.courierName", is(deliveryResponse.getCourierName())))
                .andExpect(jsonPath("$.orderCode", is(deliveryResponse.getOrderCode())));
    }

    @Test
    void assignOnCourier() throws Exception {
        String trackNo = "DLVR!01";
        String courierCode = "CODE123";

        DeliveryAssignRequest deliveryAssignRequest = DeliveryAssignRequest.builder()
                .courierCode(courierCode)
                .deliveryCode(trackNo)
                .build();

        DeliveryResponse deliveryResponse = DeliveryResponse.builder()
                .deliveryCode(trackNo)
                .courierName("Maxim")
                .orderCode(courierCode)
                .status(DeliveryStatus.ON_DELIVERY.name())
                .build();

        when(deliveryService.assignOnCourier(deliveryAssignRequest)).thenReturn(deliveryResponse);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/api/v1/delivery/assign")
                .content(new ObjectMapper().writeValueAsString(deliveryAssignRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.deliveryCode", is(deliveryResponse.getDeliveryCode())))
                .andExpect(jsonPath("$.courierName", is(deliveryResponse.getCourierName())))
                .andExpect(jsonPath("$.orderCode", is(deliveryResponse.getOrderCode())));
    }
}
