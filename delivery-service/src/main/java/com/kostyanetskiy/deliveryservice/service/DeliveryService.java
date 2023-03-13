package com.kostyanetskiy.deliveryservice.service;

import com.kostyanetskiy.deliveryservice.dto.*;
import com.kostyanetskiy.deliveryservice.event.OrderPlaceEvent;

import java.util.List;

public interface DeliveryService {
    void create(OrderPlaceEvent orderPlaceEvent);
    DeliveryResponse change(DeliveryChangeRequest deliveryRequest);
    DeliveryResponse details(String trackNo);
    DeliveryResponse assignOnCourier(DeliveryAssignRequest deliveryChangeRequest);
    List<DeliveryResponse> allDeliveriesByCourier(String courierCode);
    List<DeliveryResponse> getAll();
}
