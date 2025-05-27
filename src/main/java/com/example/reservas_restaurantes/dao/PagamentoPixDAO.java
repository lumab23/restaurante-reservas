package com.example.reservas_restaurantes.dao;

import org.springframework.stereotype.Repository;

import com.example.reservas_restaurantes.model.PagamentoPix;
import com.example.reservas_restaurantes.repository.PagamentoPixRepository;

import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@Repository
public class PagamentoPixDAO implements PagamentoPixRepository {

    private final DataSource dataSource;

    public PagamentoPixDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void releaseConnection(Connection conn) {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    @Override
    public void salvar(PagamentoPix pagamentoPix) throws SQLException {
        String sql = "INSERT INTO PagamentoPix (id_pagamento, chavePix) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);

            statement.setInt(1, pagamentoPix.getIdPagamento());
            statement.setString(2, pagamentoPix.getChavePix());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar detalhes do pagamento PIX: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }

    @Override
    public Optional<PagamentoPix> buscarPorIdPagamento(int idPagamento) throws SQLException {
        String sql = "SELECT id_pagamento, chavePix FROM PagamentoPix WHERE id_pagamento = ?";
        PagamentoPix detalhe = null;
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, idPagamento);
            rs = statement.executeQuery();
            if (rs.next()) {
                detalhe = new PagamentoPix();
                detalhe.setIdPagamento(rs.getInt("id_pagamento"));
                detalhe.setChavePix(rs.getString("chavePix"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar detalhes do pagamento PIX: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
        return Optional.ofNullable(detalhe);
    }

    @Override
    public void atualizar(PagamentoPix pagamentoPix) throws SQLException {
        String sql = "UPDATE PagamentoPix SET chavePix = ? WHERE id_pagamento = ?";
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setString(1, pagamentoPix.getChavePix());
            statement.setInt(2, pagamentoPix.getIdPagamento());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar detalhes do pagamento PIX: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }

    @Override
    public void deletar(int idPagamento) throws SQLException {
        String sql = "DELETE FROM PagamentoPix WHERE id_pagamento = ?";
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, idPagamento);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar detalhes do pagamento PIX: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }
}