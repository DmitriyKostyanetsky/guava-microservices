package com.kostyanetskiy.deliveryservice.dto;

import com.kostyanetskiy.deliveryservice.model.Delivery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourierResponse {
    private String name;
    private String code;
    private String status;
    private List<DeliveryResponse> deliveries;
}
