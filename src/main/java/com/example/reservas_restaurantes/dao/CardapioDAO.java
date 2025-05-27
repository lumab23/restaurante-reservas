package com.example.reservas_restaurantes.dao;

import org.springframework.stereotype.Repository;

import com.example.reservas_restaurantes.model.Cardapio;
import com.example.reservas_restaurantes.repository.CardapioRepository;

import org.springframework.jdbc.datasource.DataSourceUtils; // Importar DataSourceUtils

import javax.sql.DataSource; // Importar DataSource
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository // Anotação para que o Spring gerencie este componente de persistência
public class CardapioDAO implements CardapioRepository {

    private final DataSource dataSource; // O Spring injetará o DataSource configurado

    // Injeção de dependência do DataSource
    public CardapioDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Método auxiliar para obter uma conexão gerenciada pela transação do Spring
    private Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    // Método auxiliar para liberar a conexão (sem fechar se estiver em transação)
    private void releaseConnection(Connection conn) {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    @Override
    public void salvar(Cardapio itemCardapio) throws SQLException {
        String sql = "INSERT INTO Cardapio (nome, descricao, preco) VALUES (?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet chavesGeradas = null;

        try {
            connection = getConnection(); // Obter conexão do pool/transação
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            statement.setString(1, itemCardapio.getNome());
            statement.setString(2, itemCardapio.getDescricao());
            statement.setBigDecimal(3, itemCardapio.getPreco());

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao criar item do cardápio, nenhuma linha afetada.");
            }

            chavesGeradas = statement.getGeneratedKeys();
            if (chavesGeradas.next()) {
                itemCardapio.setIdCardapio(chavesGeradas.getInt(1));
            } else {
                throw new SQLException("Falha ao criar item do cardápio, nenhum ID obtido.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar item do cardápio: " + e.getMessage());
            throw e;
        } finally {
            if (chavesGeradas != null) chavesGeradas.close();
            if (statement != null) statement.close();
            releaseConnection(connection); // Liberar a conexão
        }
    }

    @Override
    public Optional<Cardapio> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_cardapio, nome, descricao, preco FROM Cardapio WHERE id_cardapio = ?";
        Cardapio itemCardapio = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            if (rs.next()) {
                itemCardapio = new Cardapio();
                itemCardapio.setIdCardapio(rs.getInt("id_cardapio"));
                itemCardapio.setNome(rs.getString("nome"));
                itemCardapio.setDescricao(rs.getString("descricao"));
                itemCardapio.setPreco(rs.getBigDecimal("preco"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar item do cardápio por ID: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
        return Optional.ofNullable(itemCardapio);
    }

    @Override
    public List<Cardapio> buscarTodos() throws SQLException {
        String sql = "SELECT id_cardapio, nome, descricao, preco FROM Cardapio";
        List<Cardapio> itensCardapio = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            while (rs.next()) {
                Cardapio itemCardapio = new Cardapio();
                itemCardapio.setIdCardapio(rs.getInt("id_cardapio"));
                itemCardapio.setNome(rs.getString("nome"));
                itemCardapio.setDescricao(rs.getString("descricao"));
                itemCardapio.setPreco(rs.getBigDecimal("preco"));
                itensCardapio.add(itemCardapio);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os itens do cardápio: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
        return itensCardapio;
    }

    @Override
    public void atualizar(Cardapio itemCardapio) throws SQLException {
        String sql = "UPDATE Cardapio SET nome = ?, descricao = ?, preco = ? WHERE id_cardapio = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, itemCardapio.getNome());
            statement.setString(2, itemCardapio.getDescricao());
            statement.setBigDecimal(3, itemCardapio.getPreco());
            statement.setInt(4, itemCardapio.getIdCardapio());
            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao atualizar item do cardápio, nenhum item encontrado com o ID: " + itemCardapio.getIdCardapio());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar item do cardápio: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM Cardapio WHERE id_cardapio = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar item do cardápio: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
    }
}