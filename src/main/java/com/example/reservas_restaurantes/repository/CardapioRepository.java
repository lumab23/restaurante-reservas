package com.example.reservas_restaurantes.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.example.reservas_restaurantes.model.Cardapio;

public interface CardapioRepository {

    void salvar(Cardapio itemCardapio) throws SQLException;

    Optional<Cardapio> buscarPorId(int id) throws SQLException;

    List<Cardapio> buscarTodos() throws SQLException;

    void atualizar(Cardapio itemCardapio) throws SQLException;

    void deletar(int id) throws SQLException;
}