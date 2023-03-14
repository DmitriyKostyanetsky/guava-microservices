package com.kostyanetskiy.deliveryservice.service;

import com.kostyanetskiy.deliveryservice.dto.CourierCreateRequest;
import com.kostyanetskiy.deliveryservice.dto.CourierResponse;
import com.kostyanetskiy.deliveryservice.enums.CourierStatus;
import com.kostyanetskiy.deliveryservice.enums.DeliveryStatus;
import com.kostyanetskiy.deliveryservice.model.Courier;
import com.kostyanetskiy.deliveryservice.model.Delivery;
import com.kostyanetskiy.deliveryservice.repository.CourierRepository;
import com.kostyanetskiy.deliveryservice.service.impl.CourierServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = CourierServiceImpl.class)
public class CourierServiceTest {

    @Autowired
    private CourierService courierService;

    @MockBean
    private CourierRepository courierRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void allCouriers() {
        List<Courier> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Courier courier = new Courier();
            courier.setStatus(CourierStatus.ON_DELIVERY);
            courier.setCode("CODE" + i);
            courier.setPassword("pass" + i);
            courier.setName("Alex");
            courier.setRole("ROLE_COURIER");

            Delivery delivery = new Delivery();
            delivery.setTrackNo("DLVR" + i);
            delivery.setOrderCode("ORDR" + i);
            delivery.setCourier(courier);
            delivery.setStatus(DeliveryStatus.ON_DELIVERY);

            courier.setDeliveries(Collections.singletonList(delivery));

            list.add(courier);
        }

        when(courierRepository.findAll()).thenReturn(list);

        List<CourierResponse> courierResponses = courierService.allCouriers();

        assertEquals(list.size(), courierResponses.size());
        courierResponses.forEach(courierResponse -> assertEquals("Alex", courierResponse.getName()));
    }

    @Test
    void createCourier() {
        String pass = "a$l$x";
        CourierCreateRequest courierCreateRequest = CourierCreateRequest.builder()
                .name("Alex")
                .password(pass)
                .build();

        when(passwordEncoder.encode(pass)).thenReturn("123");

        CourierResponse courierResponse = courierService.createCourier(courierCreateRequest);

        assertEquals(courierCreateRequest.getName(), courierResponse.getName());
        assertEquals(CourierStatus.FREE.name(), courierResponse.getStatus());
        assertTrue(courierResponse.getDeliveries().isEmpty());
        assertNotNull(courierResponse.getCode());
    }
}
