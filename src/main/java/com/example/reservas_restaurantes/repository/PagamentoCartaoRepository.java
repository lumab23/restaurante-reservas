package com.example.reservas_restaurantes.repository;

import java.sql.SQLException;
import java.util.Optional;

import com.example.reservas_restaurantes.model.PagamentoCartao;

public interface PagamentoCartaoRepository {
    void salvar(PagamentoCartao pagamentoCartao) throws SQLException;
    Optional<PagamentoCartao> buscarPorIdPagamento(int idPagamento) throws SQLException;
    void atualizar(PagamentoCartao pagamentoCartao) throws SQLException;
    void deletar(int idPagamento) throws SQLException;
}