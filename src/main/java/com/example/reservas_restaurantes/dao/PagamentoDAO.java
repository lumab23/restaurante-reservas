package com.example.reservas_restaurantes.dao;

import org.springframework.stereotype.Repository;

import com.example.reservas_restaurantes.enums.MetodoPagamento;
import com.example.reservas_restaurantes.model.Pagamento;
import com.example.reservas_restaurantes.repository.PagamentoRepository;

import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PagamentoDAO implements PagamentoRepository {

    private final DataSource dataSource;

    public PagamentoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void releaseConnection(Connection conn) {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    @Override
    public void salvar(Pagamento pagamento) throws SQLException {
        String sql = "INSERT INTO Pagamento (id_reserva, valor, metodoPagamento) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, pagamento.getIdReserva());
            statement.setBigDecimal(2, pagamento.getValor());
            statement.setString(3, pagamento.getMetodoPagamento().name());

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao criar pagamento, nenhuma linha afetada.");
            }

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                pagamento.setIdPagamento(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Falha ao criar pagamento, nenhum ID obtido.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar pagamento: " + e.getMessage());
            throw e;
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }

    @Override
    public Optional<Pagamento> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_pagamento, id_reserva, valor, metodoPagamento FROM Pagamento WHERE id_pagamento = ?";
        Pagamento pagamento = null;
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            if (rs.next()) {
                pagamento = new Pagamento();
                pagamento.setIdPagamento(rs.getInt("id_pagamento"));
                pagamento.setIdReserva(rs.getInt("id_reserva"));
                pagamento.setValor(rs.getBigDecimal("valor"));
                pagamento.setMetodoPagamento(MetodoPagamento.valueOf(rs.getString("metodoPagamento")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar pagamento por ID: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
        return Optional.ofNullable(pagamento);
    }

    @Override
    public List<Pagamento> buscarPorReservaId(int idReserva) throws SQLException {
        String sql = "SELECT id_pagamento, id_reserva, valor, metodoPagamento FROM Pagamento WHERE id_reserva = ?";
        List<Pagamento> pagamentos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, idReserva);
            rs = statement.executeQuery();
            while (rs.next()) {
                Pagamento pagamento = new Pagamento();
                pagamento.setIdPagamento(rs.getInt("id_pagamento"));
                pagamento.setIdReserva(rs.getInt("id_reserva"));
                pagamento.setValor(rs.getBigDecimal("valor"));
                pagamento.setMetodoPagamento(MetodoPagamento.valueOf(rs.getString("metodoPagamento")));
                pagamentos.add(pagamento);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar pagamentos por ID da reserva: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
        return pagamentos;
    }

    @Override
    public List<Pagamento> buscarTodos() throws SQLException {
        String sql = "SELECT id_pagamento, id_reserva, valor, metodoPagamento FROM Pagamento";
        List<Pagamento> pagamentos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            rs = statement.executeQuery();
            while (rs.next()) {
                Pagamento pagamento = new Pagamento();
                pagamento.setIdPagamento(rs.getInt("id_pagamento"));
                pagamento.setIdReserva(rs.getInt("id_reserva"));
                pagamento.setValor(rs.getBigDecimal("valor"));
                pagamento.setMetodoPagamento(MetodoPagamento.valueOf(rs.getString("metodoPagamento")));
                pagamentos.add(pagamento);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os pagamentos: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
        return pagamentos;
    }


    @Override
    public void atualizar(Pagamento pagamento) throws SQLException {
        String sql = "UPDATE Pagamento SET id_reserva = ?, valor = ?, metodoPagamento = ? WHERE id_pagamento = ?";
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, pagamento.getIdReserva());
            statement.setBigDecimal(2, pagamento.getValor());
            statement.setString(3, pagamento.getMetodoPagamento().name());
            statement.setInt(4, pagamento.getIdPagamento());

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao atualizar pagamento, nenhum pagamento encontrado com o ID: " + pagamento.getIdPagamento());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar pagamento: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM Pagamento WHERE id_pagamento = ?";
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar pagamento: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }
}