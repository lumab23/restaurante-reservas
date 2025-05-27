package com.example.reservas_restaurantes.dao;

import org.springframework.stereotype.Repository;

import com.example.reservas_restaurantes.enums.StatusMesa;
import com.example.reservas_restaurantes.model.Mesa;
import com.example.reservas_restaurantes.repository.MesaRepository;

import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MesaDAO implements MesaRepository {

    private final DataSource dataSource;

    public MesaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void releaseConnection(Connection conn) {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    @Override
    public void salvar(Mesa mesa) throws SQLException {
        String sql = "INSERT INTO Mesa (capacidade, localizacao, statusMesa) VALUES (?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, mesa.getCapacidade());
            statement.setString(2, mesa.getLocalizacao());
            statement.setString(3, mesa.getStatusMesa().name());

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao criar mesa, nenhuma linha afetada.");
            }

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                mesa.setIdMesa(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Falha ao criar mesa, nenhum ID obtido.");
            }

        } catch (SQLException e) {
            System.err.println("erro ao salvar mesa: " + e.getMessage());
            throw e;
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
    }

    @Override
    public Optional<Mesa> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_mesa, capacidade, localizacao, statusMesa FROM Mesa WHERE id_mesa = ?";
        Mesa mesa = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            if (rs.next()) {
                mesa = new Mesa();
                mesa.setIdMesa(rs.getInt("id_mesa"));
                mesa.setCapacidade(rs.getInt("capacidade"));
                mesa.setLocalizacao(rs.getString("localizacao"));
                mesa.setStatusMesa(StatusMesa.valueOf(rs.getString("statusMesa")));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar mesa por ID: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
        return Optional.ofNullable(mesa);
    }

    @Override
    public List<Mesa> buscarTodos() throws SQLException {
        String sql = "SELECT id_mesa, capacidade, localizacao, statusMesa FROM Mesa";
        List<Mesa> mesas = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            while (rs.next()) {
                Mesa mesa = new Mesa();
                mesa.setIdMesa(rs.getInt("id_mesa"));
                mesa.setCapacidade(rs.getInt("capacidade"));
                mesa.setLocalizacao(rs.getString("localizacao"));
                mesa.setStatusMesa(StatusMesa.valueOf(rs.getString("statusMesa")));
                mesas.add(mesa);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todas as mesas: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
        return mesas;
    }

    @Override
    public void atualizar(Mesa mesa) throws SQLException {
        String sql = "UPDATE Mesa SET capacidade = ?, localizacao = ?, statusMesa = ? WHERE id_mesa = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);

            statement.setInt(1, mesa.getCapacidade());
            statement.setString(2, mesa.getLocalizacao());
            statement.setString(3, mesa.getStatusMesa().name());
            statement.setInt(4, mesa.getIdMesa());

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao atualizar mesa, nenhuma mesa encontrada com o ID: " + mesa.getIdMesa());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar mesa: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
    }

    @Override
    public void atualizarStatus(int idMesa, StatusMesa novoStatus) throws SQLException {
        String sql = "UPDATE Mesa SET statusMesa = ? WHERE id_mesa = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, novoStatus.name());
            statement.setInt(2, idMesa);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao atualizar status da mesa, nenhuma mesa encontrada com o ID: " + idMesa);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status da mesa: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM Mesa WHERE id_mesa = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar mesa: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
    }
}