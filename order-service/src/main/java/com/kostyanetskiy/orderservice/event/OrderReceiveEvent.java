package com.kostyanetskiy.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderReceiveEvent {
    private String trackNo;
    private String orderCode;
    private String courierName;
    private String status;
}
