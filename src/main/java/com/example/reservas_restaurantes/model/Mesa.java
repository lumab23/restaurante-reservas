package com.example.reservas_restaurantes.model;

import com.example.reservas_restaurantes.enums.StatusMesa;

public class Mesa {
    
    private int idMesa;
    private int capacidade;
    private String localizacao;
    private StatusMesa statusMesa;

    public Mesa() {
    }

    public Mesa(int capacidade, String localizacao, StatusMesa statusMesa) {
        setCapacidade(capacidade);
        setLocalizacao(localizacao);
        setStatusMesa(statusMesa);
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        if (idMesa < 0) {
            throw new IllegalArgumentException("ID da mesa não pode ser negativo");
        }
        this.idMesa = idMesa;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(int capacidade) {
        if (capacidade <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser maior que zero");
        }
        this.capacidade = capacidade;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        if (localizacao == null || localizacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Localização não pode ser nula ou vazia");
        }
        this.localizacao = localizacao.trim();
    }

    public StatusMesa getStatusMesa() {
        return statusMesa;
    }

    public void setStatusMesa(StatusMesa statusMesa) {
        if (statusMesa == null) {
            throw new IllegalArgumentException("Status da mesa não pode ser nulo");
        }
        this.statusMesa = statusMesa;
    }

    @Override
    public String toString() {
        return "Mesa [idMesa=" + idMesa + ", capacidade=" + capacidade + ", localizacao=" + localizacao
                + ", statusMesa=" + statusMesa + "]";
    }

    
    
} 