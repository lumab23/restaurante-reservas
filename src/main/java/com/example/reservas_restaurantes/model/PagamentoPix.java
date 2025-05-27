package com.example.reservas_restaurantes.model;


public class PagamentoPix {
    private int idPagamento;
    private String chavePix;

    public PagamentoPix() {
    }

    public PagamentoPix(int idPagamento, String chavePix) {
        this.idPagamento = idPagamento;
        this.chavePix = chavePix;
    }

    public int getIdPagamento() {
        return idPagamento;
    }

    public void setIdPagamento(int idPagamento) {
        this.idPagamento = idPagamento;
    }

    public String getChavePix() {
        return chavePix;
    }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    @Override
    public String toString() {
        return "PagamentoPix{" +
                "idPagamento=" + idPagamento +
                ", chavePix='" + chavePix + '\'' +
                '}';
    }
}