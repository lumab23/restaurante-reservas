package com.example.reservas_restaurantes.dao;

import org.springframework.stereotype.Repository;

import com.example.reservas_restaurantes.model.Pedido;
import com.example.reservas_restaurantes.repository.PedidoRepository;

import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PedidoDAO implements PedidoRepository {

    private final DataSource dataSource;

    public PedidoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void releaseConnection(Connection conn) {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    @Override
    public void salvar(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO Pedido (id_reserva, dataHora) VALUES (?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, pedido.getIdReserva());
            statement.setTimestamp(2, Timestamp.valueOf(pedido.getDataHora()));

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao criar pedido, nenhuma linha afetada.");
            }

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                pedido.setIdPedido(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Falha ao criar pedido, nenhum ID obtido.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar pedido: " + e.getMessage());
            throw e;
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
    }

    @Override
    public Optional<Pedido> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_pedido, id_reserva, dataHora FROM Pedido WHERE id_pedido = ?";
        Pedido pedido = null;
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            if (rs.next()) {
                pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("id_pedido"));
                pedido.setIdReserva(rs.getInt("id_reserva"));
                pedido.setDataHora(rs.getTimestamp("dataHora").toLocalDateTime());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar pedido por ID: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
        return Optional.ofNullable(pedido);
    }

    @Override
    public List<Pedido> buscarTodos() throws SQLException {
        String sql = "SELECT id_pedido, id_reserva, dataHora FROM Pedido";
        List<Pedido> pedidos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            rs = statement.executeQuery();
            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("id_pedido"));
                pedido.setIdReserva(rs.getInt("id_reserva"));
                pedido.setDataHora(rs.getTimestamp("dataHora").toLocalDateTime());
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os pedidos: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
        return pedidos;
    }

    @Override
    public List<Pedido> buscarPorReserva(int idReserva) throws SQLException {
        String sql = "SELECT id_pedido, id_reserva, dataHora FROM Pedido WHERE id_reserva = ?";
        List<Pedido> pedidos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, idReserva);
            rs = statement.executeQuery();
            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setIdPedido(rs.getInt("id_pedido"));
                pedido.setIdReserva(rs.getInt("id_reserva"));
                pedido.setDataHora(rs.getTimestamp("dataHora").toLocalDateTime());
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar pedidos por reserva: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
        return pedidos;
    }


    @Override
    public void atualizar(Pedido pedido) throws SQLException {
        String sql = "UPDATE Pedido SET id_reserva = ?, dataHora = ? WHERE id_pedido = ?";
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, pedido.getIdReserva());
            statement.setTimestamp(2, Timestamp.valueOf(pedido.getDataHora()));
            statement.setInt(3, pedido.getIdPedido());

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao atualizar pedido, nenhum pedido encontrado com o ID: " + pedido.getIdPedido());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar pedido: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM Pedido WHERE id_pedido = ?";
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar pedido: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }
}