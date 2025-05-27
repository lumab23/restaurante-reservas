package com.example.reservas_restaurantes.exception;

public class BusinessRuleException extends Exception {

    public BusinessRuleException(String message) {
        super(message);
    }

    public BusinessRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}