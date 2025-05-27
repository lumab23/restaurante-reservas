package com.example.reservas_restaurantes.dao;
import org.springframework.stereotype.Repository;

import com.example.reservas_restaurantes.model.ItemPedido;
import com.example.reservas_restaurantes.repository.ItemPedidoRepository;

import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ItemPedidoDAO implements ItemPedidoRepository {

    private final DataSource dataSource;

    public ItemPedidoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void releaseConnection(Connection conn) {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    @Override
    public void salvar(ItemPedido itemPedido) throws SQLException {
        String sql = "INSERT INTO ItemPedido (id_pedido, id_cardapio, quantidade) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, itemPedido.getIdPedido());
            statement.setInt(2, itemPedido.getIdCardapio());
            statement.setInt(3, itemPedido.getQuantidade());

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao criar item do pedido, nenhuma linha afetada.");
            }

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                itemPedido.setIdItemPedido(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Falha ao criar item do pedido, nenhum ID obtido.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar item do pedido: " + e.getMessage());
            throw e;
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }

    @Override
    public Optional<ItemPedido> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_item_pedido, id_pedido, id_cardapio, quantidade FROM ItemPedido WHERE id_item_pedido = ?";
        ItemPedido item = null;
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            if (rs.next()) {
                item = new ItemPedido();
                item.setIdItemPedido(rs.getInt("id_item_pedido"));
                item.setIdPedido(rs.getInt("id_pedido"));
                item.setIdCardapio(rs.getInt("id_cardapio"));
                item.setQuantidade(rs.getInt("quantidade"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar item do pedido por ID: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
        return Optional.ofNullable(item);
    }

    @Override
    public List<ItemPedido> buscarPorPedidoId(int idPedido) throws SQLException {
        String sql = "SELECT id_item_pedido, id_pedido, id_cardapio, quantidade FROM ItemPedido WHERE id_pedido = ?";
        List<ItemPedido> itens = new ArrayList<>();
        Connection conn = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, idPedido);
            rs = statement.executeQuery();
            while (rs.next()) {
                ItemPedido item = new ItemPedido();
                item.setIdItemPedido(rs.getInt("id_item_pedido"));
                item.setIdPedido(rs.getInt("id_pedido"));
                item.setIdCardapio(rs.getInt("id_cardapio"));
                item.setQuantidade(rs.getInt("quantidade"));
                itens.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar itens por ID do pedido: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
        return itens;
    }

    @Override
    public void atualizar(ItemPedido itemPedido) throws SQLException {
        String sql = "UPDATE ItemPedido SET id_pedido = ?, id_cardapio = ?, quantidade = ? WHERE id_item_pedido = ?";
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, itemPedido.getIdPedido());
            statement.setInt(2, itemPedido.getIdCardapio());
            statement.setInt(3, itemPedido.getQuantidade());
            statement.setInt(4, itemPedido.getIdItemPedido());

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao atualizar item do pedido, nenhum item encontrado com o ID: " + itemPedido.getIdItemPedido());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar item do pedido: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM ItemPedido WHERE id_item_pedido = ?";
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar item do pedido: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }

    @Override
    public void deletarPorPedidoId(int idPedido) throws SQLException {
        String sql = "DELETE FROM ItemPedido WHERE id_pedido = ?";
        Connection conn = null;
        PreparedStatement statement = null;

        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            statement.setInt(1, idPedido);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar itens pelo ID do pedido: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(conn);
        }
    }
}