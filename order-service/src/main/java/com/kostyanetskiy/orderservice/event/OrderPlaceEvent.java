package com.kostyanetskiy.orderservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPlaceEvent {
    private String orderCode;
    private String receiver;
    private String address;
    private String itemName;
}
