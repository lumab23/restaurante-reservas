package com.example.reservas_restaurantes.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.example.reservas_restaurantes.model.Pedido;

public interface PedidoRepository {
    void salvar(Pedido pedido) throws SQLException;
    Optional<Pedido> buscarPorId(int id) throws SQLException;
    List<Pedido> buscarTodos() throws SQLException;
    List<Pedido> buscarPorReserva(int idReserva) throws SQLException;
    void atualizar(Pedido pedido) throws SQLException;
    void deletar(int id) throws SQLException;
}