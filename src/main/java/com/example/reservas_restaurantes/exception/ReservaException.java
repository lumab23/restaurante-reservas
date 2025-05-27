package com.example.reservas_restaurantes.exception;

public class ReservaException extends BusinessRuleException { // Ou extends Exception diretamente

    public ReservaException(String message) {
        super(message);
    }

    public ReservaException(String message, Throwable cause) {
        super(message, cause);
    }
}
