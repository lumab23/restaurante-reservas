package com.example.reservas_restaurantes.model;

import java.time.LocalDateTime;

public class Pedido {
    private int idPedido;
    private int idReserva; // fk para reserva
    private LocalDateTime dataHora;

    public Pedido() {
    }

    public Pedido(int idReserva, LocalDateTime dataHora) {
        setIdReserva(idReserva);
        setDataHora(dataHora);
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        if (idPedido < 0) {
            throw new IllegalArgumentException("ID do pedido não pode ser negativo");
        }
        this.idPedido = idPedido;
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

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            throw new IllegalArgumentException("Data e hora não podem ser nulas");
        }
        if (dataHora.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data e hora não podem ser no futuro");
        }
        this.dataHora = dataHora;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "idPedido=" + idPedido +
                ", idReserva=" + idReserva +
                ", dataHora=" + dataHora +
                '}';
    }
}