package com.kostyanetskiy.deliveryservice.service;

import com.kostyanetskiy.deliveryservice.dto.CourierCreateRequest;
import com.kostyanetskiy.deliveryservice.dto.CourierResponse;

import java.util.List;

public interface CourierService {
    CourierResponse createCourier(CourierCreateRequest courierCreateRequest);
    List<CourierResponse> allCouriers();
}
