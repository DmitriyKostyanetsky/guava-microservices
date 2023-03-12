package com.kostyanetskiy.deliveryservice.dto;


import com.kostyanetskiy.deliveryservice.enums.CourierStatus;
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
    private CourierStatus status;
    private List<Delivery> deliveries;
}
