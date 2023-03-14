package com.kostyanetskiy.orderservice.exception;

public class UnrecognizedOrderException extends RuntimeException {
    public UnrecognizedOrderException(String s) {
        super(s);
    }
}
