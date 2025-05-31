package com.example.reservas_restaurantes.dao;

import org.springframework.stereotype.Repository;

import com.example.reservas_restaurantes.model.PagamentoCartao;
import com.example.reservas_restaurantes.repository.PagamentoCartaoRepository;

import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@Repository
public class PagamentoCartaoDAO implements PagamentoCartaoRepository {

    private final DataSource dataSource;

    public PagamentoCartaoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void releaseConnection(Connection conn) {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    @Override
    public void salvar(PagamentoCartao pagamentoCartao) throws SQLException {
        String sql = "INSERT INTO PagamentoCartao (id_pagamento, numeroCartao, titular, validade, cvv) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);

            statement.setInt(1, pagamentoCartao.getIdPagamento());
            statement.setString(2, pagamentoCartao.getNumeroCartao());
            statement.setString(3, pagamentoCartao.getTitular());
            statement.setDate(4, java.sql.Date.valueOf(pagamentoCartao.getValidade()));
            statement.setString(5, pagamentoCartao.getCvv());

            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar detalhes do pagamento com cartão: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }

    @Override
    public Optional<PagamentoCartao> buscarPorIdPagamento(int idPagamento) throws SQLException {
        String sql = "SELECT id_pagamento, numeroCartao, titular, validade, cvv FROM PagamentoCartao WHERE id_pagamento = ?";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        PagamentoCartao pagamentoCartao = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, idPagamento);
            rs = statement.executeQuery();

            if (rs.next()) {
                pagamentoCartao = new PagamentoCartao();
                pagamentoCartao.setIdPagamento(rs.getInt("id_pagamento"));
                pagamentoCartao.setNumeroCartao(rs.getString("numeroCartao"));
                pagamentoCartao.setTitular(rs.getString("titular"));
                pagamentoCartao.setValidade(rs.getDate("validade").toLocalDate());
                pagamentoCartao.setCvv(rs.getString("cvv"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar detalhes do pagamento com cartão: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
        return Optional.ofNullable(pagamentoCartao);
    }

    @Override
    public void atualizar(PagamentoCartao pagamentoCartao) throws SQLException {
        String sql = "UPDATE PagamentoCartao SET numeroCartao = ?, titular = ?, validade = ?, cvv = ? WHERE id_pagamento = ?";
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);

            statement.setString(1, pagamentoCartao.getNumeroCartao());
            statement.setString(2, pagamentoCartao.getTitular());
            statement.setDate(3, java.sql.Date.valueOf(pagamentoCartao.getValidade()));
            statement.setString(4, pagamentoCartao.getCvv());
            statement.setInt(5, pagamentoCartao.getIdPagamento());

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao atualizar pagamento com cartão, nenhum registro encontrado com o ID: " + pagamentoCartao.getIdPagamento());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar detalhes do pagamento com cartão: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }

    @Override
    public void deletar(int idPagamento) throws SQLException {
        String sql = "DELETE FROM PagamentoCartao WHERE id_pagamento = ?";
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, idPagamento);

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao deletar pagamento com cartão, nenhum registro encontrado com o ID: " + idPagamento);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao deletar detalhes do pagamento com cartão: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }
} 