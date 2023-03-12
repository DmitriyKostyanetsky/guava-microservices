package com.kostyanetskiy.deliveryservice.service.impl;

import com.kostyanetskiy.deliveryservice.dto.CourierCreateRequest;
import com.kostyanetskiy.deliveryservice.dto.CourierResponse;
import com.kostyanetskiy.deliveryservice.enums.CourierStatus;
import com.kostyanetskiy.deliveryservice.model.Courier;
import com.kostyanetskiy.deliveryservice.repository.CourierRepository;
import com.kostyanetskiy.deliveryservice.service.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CourierServiceImpl implements CourierService {

    private final CourierRepository courierRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CourierResponse createCourier(CourierCreateRequest courierCreateRequest) {
        String lUUID = String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
        Courier courier = new Courier();
        courier.setCode(String.format("COUR%s", lUUID));
        courier.setName(courierCreateRequest.getName());
        courier.setPassword(passwordEncoder.encode(courierCreateRequest.getPassword()));
        courier.setStatus(CourierStatus.FREE);
        courier.setRole("ROLE_COURIER");

        courierRepository.save(courier);

        return buildCourierResponse(courier);
    }

    @Override
    public List<CourierResponse> allCouriers() {
        return courierRepository.findAll().stream()
                .map(this::buildCourierResponse)
                .collect(Collectors.toList());
    }

    private CourierResponse buildCourierResponse(Courier courier) {
        return CourierResponse.builder()
                .code(courier.getCode())
                .name(courier.getName())
                .status(courier.getStatus())
                .deliveries(courier.getDeliveries().isEmpty() ? Collections.emptyList() : courier.getDeliveries())
                .build();
    }

}
