package com.kostyanetskiy.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryResponse {
    private String orderCode;
    private String deliveryCode;
    private String status;
    private String courierName;
}
