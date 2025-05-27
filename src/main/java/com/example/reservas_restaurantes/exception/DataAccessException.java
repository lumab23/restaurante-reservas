package com.example.reservas_restaurantes.exception;

public class DataAccessException extends Exception { // Pode ser RuntimeException se preferir não checá-la sempre

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}