package com.example.reservas_restaurantes.model;

import java.time.LocalDate;

public class PagamentoCartao {
    private int idPagamento;
    private String numeroCartao;
    private String titular;
    private LocalDate validade;
    private String cvv;

    public PagamentoCartao() {
    }

    public PagamentoCartao(int idPagamento, String numeroCartao, String titular, LocalDate validade, String cvv) {
        this.idPagamento = idPagamento;
        this.numeroCartao = numeroCartao;
        this.titular = titular;
        this.validade = validade;
        this.cvv = cvv;
    }

    public int getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(int idPagamento) {
        this.idPagamento = idPagamento;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public LocalDate getValidade() {
        return validade;
    }

    public void setValidade(LocalDate validade) {
        this.validade = validade;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    @Override
    public String toString() {
        return "PagamentoCartao{" +
                "idPagamento=" + idPagamento +
                ", numeroCartao='" + "XXXX-XXXX-XXXX-" + (numeroCartao != null && numeroCartao.length() > 4 ? numeroCartao.substring(numeroCartao.length() - 4) : "XXXX") + '\'' + // Mascarado
                ", titular='" + titular + '\'' +
                ", validade=" + validade +
                ", cvv='" + "***" + '\'' + // Mascarado
                '}';
    }
}