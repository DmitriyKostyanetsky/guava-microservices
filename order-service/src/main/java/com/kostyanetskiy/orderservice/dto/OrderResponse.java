package com.kostyanetskiy.orderservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {
    private String code;
    private String trackNo;
    private String address;
    private String receiver;
    private String status;
    private Date createdDate;
    private String itemName;
}
