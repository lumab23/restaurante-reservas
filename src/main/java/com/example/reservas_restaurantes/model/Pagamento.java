package com.example.reservas_restaurantes.model;

import java.math.BigDecimal;

public class Pagamento {
    private int idPagamento;
    private int idReserva;
    private BigDecimal valor;
    private String metodoPagamento;

    public Pagamento() {
    }

    public Pagamento(int idReserva, BigDecimal valor) {
        this.idReserva = idReserva;
        this.valor = valor;
        this.metodoPagamento = "CARTAO";
    }

    public int getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(int idPagamento) {
        if (idPagamento < 0) {
            throw new IllegalArgumentException("ID do pagamento não pode ser negativo");
        }
        this.idPagamento = idPagamento;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        if (idReserva <= 0) {
            throw new IllegalArgumentException("ID da reserva deve ser maior que zero");
        }
        this.idReserva = idReserva;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
        this.valor = valor;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        if (metodoPagamento == null) {
            throw new IllegalArgumentException("Método de pagamento não pode ser nulo");
        }
        this.metodoPagamento = metodoPagamento;
    }

    @Override
    public String toString() {
        return "Pagamento{" +
                "idPagamento=" + idPagamento +
                ", idReserva=" + idReserva +
                ", valor=" + valor +
                ", metodoPagamento=" + metodoPagamento +
                '}';
    }
}