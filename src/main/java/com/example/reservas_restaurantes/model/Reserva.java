package com.example.reservas_restaurantes.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.reservas_restaurantes.enums.StatusReserva;
import com.example.reservas_restaurantes.enums.TipoOcasiao;

public class Reserva {
    private int idReserva;
    private int idCliente; // fk para cliente
    private int idMesa; // fk para mesa
    private LocalDateTime dataHora;
    private int numPessoas;
    private TipoOcasiao ocasiao;
    private StatusReserva statusReserva;
    private String observacao;
    private boolean isNewReservation = true; // Flag para controlar se é uma nova reserva

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Reserva() {
        this.isNewReservation = false; // Construtor vazio é usado para leitura
    }

    public Reserva(int idCliente, int idMesa, LocalDateTime dataHora, int numPessoas, TipoOcasiao ocasiao, StatusReserva statusReserva, String observacao) {
        this.isNewReservation = true; // Construtor com parâmetros é usado para criação
        setIdCliente(idCliente);
        setIdMesa(idMesa);
        setDataHora(dataHora);
        setNumPessoas(numPessoas);
        setOcasiao(ocasiao);
        setStatusReserva(statusReserva);
        setObservacao(observacao);
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        if (idReserva < 0) {
            throw new IllegalArgumentException("ID da reserva não pode ser negativo");
        }
        this.idReserva = idReserva;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        if (idCliente <= 0) {
            throw new IllegalArgumentException("ID do cliente deve ser maior que zero");
        }
        this.idCliente = idCliente;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        if (idMesa <= 0) {
            throw new IllegalArgumentException("ID da mesa deve ser maior que zero");
        }
        this.idMesa = idMesa;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            throw new IllegalArgumentException("Data e hora não podem ser nulas");
        }
        if (isNewReservation && dataHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data e hora não podem ser no passado");
        }
        this.dataHora = dataHora;
    }

    public int getNumPessoas() {
        return numPessoas;
    }

    public void setNumPessoas(int numPessoas) {
        if (numPessoas <= 0) {
            throw new IllegalArgumentException("Número de pessoas deve ser maior que zero");
        }
        this.numPessoas = numPessoas;
    }

    public TipoOcasiao getOcasiao() {
        return ocasiao;
    }

    public void setOcasiao(TipoOcasiao ocasiao) {
        if (ocasiao == null) {
            throw new IllegalArgumentException("Ocasiao não pode ser nula");
        }
        this.ocasiao = ocasiao;
    }

    public StatusReserva getStatusReserva() {
        return statusReserva;
    }

    public void setStatusReserva(StatusReserva statusReserva) {
        if (statusReserva == null) {
            throw new IllegalArgumentException("Status da reserva não pode ser nulo");
        }
        this.statusReserva = statusReserva;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao != null ? observacao.trim() : null;
    }

    @Override
    public String toString() {
        return String.format("Reserva #%d - Mesa %d - %s - %d pessoas - %s",
            idReserva,
            idMesa,
            dataHora.format(DATETIME_FORMATTER),
            numPessoas,
            statusReserva);
    }
} 