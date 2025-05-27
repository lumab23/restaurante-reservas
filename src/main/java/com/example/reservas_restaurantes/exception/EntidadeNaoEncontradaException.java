package com.example.reservas_restaurantes.exception;

public class EntidadeNaoEncontradaException extends BusinessRuleException { // Ou extends Exception diretamente

    public EntidadeNaoEncontradaException(String message) {
        super(message);
    }

    public EntidadeNaoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}