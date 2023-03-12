package com.kostyanetskiy.deliveryservice.dto;

import com.kostyanetskiy.deliveryservice.enums.DeliveryStatus;
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
    private DeliveryStatus status;
    private String courierName;
}
