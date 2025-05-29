package com.example.reservas_restaurantes.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.example.reservas_restaurantes.model.Admin;

public interface AdminRepository {
    void salvar(Admin admin) throws SQLException;
    Optional<Admin> buscarPorId(int id) throws SQLException;
    Optional<Admin> buscarPorEmail(String email) throws SQLException;
    List<Admin> buscarTodos() throws SQLException;
    void atualizar(Admin admin) throws SQLException;
    void deletar(int id) throws SQLException;
    boolean autenticar(String email, String senha) throws SQLException;
} 