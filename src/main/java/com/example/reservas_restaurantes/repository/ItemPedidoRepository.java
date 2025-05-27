package com.example.reservas_restaurantes.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.example.reservas_restaurantes.model.ItemPedido;

public interface ItemPedidoRepository {
    void salvar(ItemPedido itemPedido) throws SQLException;
    Optional<ItemPedido> buscarPorId(int id) throws SQLException;
    List<ItemPedido> buscarPorPedidoId(int idPedido) throws SQLException;
    void atualizar(ItemPedido itemPedido) throws SQLException;
    void deletar(int id) throws SQLException;
    void deletarPorPedidoId(int idPedido) throws SQLException;
}