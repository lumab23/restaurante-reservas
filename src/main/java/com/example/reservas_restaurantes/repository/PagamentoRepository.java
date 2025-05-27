package com.example.reservas_restaurantes.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.example.reservas_restaurantes.model.Pagamento;

public interface PagamentoRepository {
    void salvar(Pagamento pagamento) throws SQLException;
    Optional<Pagamento> buscarPorId(int id) throws SQLException;
    List<Pagamento> buscarPorReservaId(int idReserva) throws SQLException;
    List<Pagamento> buscarTodos() throws SQLException;
    void atualizar(Pagamento pagamento) throws SQLException;
    void deletar(int id) throws SQLException;
}