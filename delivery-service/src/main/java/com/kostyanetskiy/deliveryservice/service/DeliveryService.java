package com.kostyanetskiy.deliveryservice.service;

import com.kostyanetskiy.deliveryservice.dto.*;

import java.util.List;

public interface DeliveryService {
    DeliveryResponse create(DeliveryCreateRequest deliveryCreateRequest);
    DeliveryResponse change(DeliveryChangeRequest deliveryRequest);
    DeliveryResponse details(String trackNo);
    DeliveryResponse assignOnCourier(DeliveryAssignRequest deliveryChangeRequest);
    List<DeliveryResponse> allDeliveriesByCourier(String courierCode);
    List<DeliveryResponse> getAll();
}
