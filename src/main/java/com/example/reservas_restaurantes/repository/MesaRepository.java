package com.example.reservas_restaurantes.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.example.reservas_restaurantes.enums.StatusMesa;
import com.example.reservas_restaurantes.model.Mesa;

public interface MesaRepository {

    void salvar(Mesa mesa) throws SQLException;

    Optional<Mesa> buscarPorId(int id) throws SQLException;

    List<Mesa> buscarTodos() throws SQLException;

    void atualizar(Mesa mesa) throws SQLException;

    void atualizarStatus(int idMesa, StatusMesa novoStatus) throws SQLException;

    void deletar(int id) throws SQLException;

}