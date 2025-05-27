package com.example.reservas_restaurantes.repository;

import java.sql.SQLException;
import java.util.Optional;

import com.example.reservas_restaurantes.model.PagamentoPix;

public interface PagamentoPixRepository {
    void salvar(PagamentoPix pagamentoPix) throws SQLException;
    Optional<PagamentoPix> buscarPorIdPagamento(int idPagamento) throws SQLException;
    void atualizar(PagamentoPix pagamentoPix) throws SQLException;
    void deletar(int idPagamento) throws SQLException;
}