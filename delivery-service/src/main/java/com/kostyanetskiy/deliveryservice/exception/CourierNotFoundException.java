package com.kostyanetskiy.deliveryservice.exception;

public class CourierNotFoundException extends RuntimeException {
    public CourierNotFoundException(String mes) {
        super(mes);
    }
}
