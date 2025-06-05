package com.example.reservas_restaurantes.dao;

import org.springframework.stereotype.Component;
import com.example.reservas_restaurantes.model.Cliente;
import com.example.reservas_restaurantes.repository.ClienteRepository;
import com.example.reservas_restaurantes.exception.BusinessRuleException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ClienteDAO implements ClienteRepository {

    private final DataSource dataSource;

    public ClienteDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void releaseConnection(Connection conn) {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    @Override
    public void salvar(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO Cliente (nome, telefone, email, dataNascimento) VALUES (?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet chavesGeradas = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, cliente.getNome());
            statement.setString(2, cliente.getTelefone());
            statement.setString(3, cliente.getEmail());

            if (cliente.getDataNascimento() != null) {
                statement.setDate(4, Date.valueOf(cliente.getDataNascimento()));
            } else {
                statement.setNull(4, Types.DATE);
            }

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Erro ao inserir o cliente no banco de dados.");
            }

            chavesGeradas = statement.getGeneratedKeys();
            if (chavesGeradas.next()) {
                cliente.setId(chavesGeradas.getInt(1));
            } else {
                throw new SQLException("falha ao criar cliente, nenhum ID obtido");
            }
        } catch (SQLException e) {
            System.err.println("erro ao salvar o cliente: " + e.getMessage());
            throw e;
        } finally {
            if (chavesGeradas != null) chavesGeradas.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) throws SQLException {
        String sql =  "SELECT id_cliente, nome, telefone, email, dataNascimento FROM Cliente WHERE id_cliente = ?";
        Cliente cliente = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            if (rs.next()) {
                cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setNome(rs.getString("nome"));
                cliente.setTelefone(rs.getString("telefone"));
                cliente.setEmail(rs.getString("email"));
                Date dataNascimento = rs.getDate("dataNascimento");
                if (dataNascimento != null) {
                    cliente.setDataNascimento(dataNascimento.toLocalDate());
                }
            }
        } catch (SQLException e) {
            System.err.println("erro ao buscar cliente por id: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
        return Optional.ofNullable(cliente);
    }

    @Override
    public List<Cliente> buscarTodos() throws SQLException {
        String sql = "SELECT id_cliente, nome, telefone, email, dataNascimento FROM Cliente";
        List<Cliente> clientes = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setNome(rs.getString("nome"));
                cliente.setTelefone(rs.getString("telefone"));
                cliente.setEmail(rs.getString("email"));
                Date dataNascimento = rs.getDate("dataNascimento");
                if (dataNascimento != null) {
                    cliente.setDataNascimento(dataNascimento.toLocalDate());
                }
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os clientes: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
        return clientes;
    }

    @Override
    public void atualizar(Cliente cliente) throws SQLException {
        String sql = "UPDATE Cliente SET nome = ?, telefone = ?, email = ?, dataNascimento = ? WHERE id_cliente = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, cliente.getNome());
            statement.setString(2, cliente.getTelefone());
            statement.setString(3, cliente.getEmail());
            if (cliente.getDataNascimento() != null) {
                statement.setDate(4, Date.valueOf(cliente.getDataNascimento()));
            } else {
                statement.setNull(4, Types.DATE);
            }
            statement.setInt(5, cliente.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao atualizar cliente, nenhum cliente encontrado com o ID: " + cliente.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM Cliente WHERE id_cliente = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);

            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Nenhum cliente encontrado com o ID: " + id + " para deletar, ou já foi deletado.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao deletar cliente: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) throws BusinessRuleException {
        String sql = "SELECT id_cliente, nome, telefone, email, dataNascimento FROM Cliente WHERE email = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            rs = statement.executeQuery();
            
            if (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setNome(rs.getString("nome"));
                cliente.setTelefone(rs.getString("telefone"));
                cliente.setEmail(rs.getString("email"));
                Date dataNascimento = rs.getDate("dataNascimento");
                if (dataNascimento != null) {
                    cliente.setDataNascimento(dataNascimento.toLocalDate());
                }
                return Optional.of(cliente);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao buscar cliente por email: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (statement != null) statement.close();
                releaseConnection(connection);
            } catch (SQLException e) {
                throw new BusinessRuleException("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}