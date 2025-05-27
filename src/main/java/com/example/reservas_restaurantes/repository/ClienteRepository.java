package com.example.reservas_restaurantes.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.example.reservas_restaurantes.model.Cliente;

public interface ClienteRepository {

    void salvar(Cliente cliente) throws SQLException;

    Optional<Cliente> buscarPorId(int idCliente) throws  SQLException;

    List<Cliente> buscarTodos() throws SQLException;

    void atualizar(Cliente cliente) throws SQLException;

    void deletar(int idCliente) throws SQLException;

}