package com.example.reservas_restaurantes.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.example.reservas_restaurantes.model.Cliente;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Primary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.repository.ClienteRepository;

@Repository
@Primary
public class ClienteDAO implements ClienteRepository {

    private static final Logger log = LoggerFactory.getLogger(ClienteDAO.class);

    private final JdbcTemplate jdbcTemplate;

    public ClienteDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void salvar(Cliente cliente) throws SQLException {
        try {
            // Primeiro, inserir o cliente
            String sql = "INSERT INTO cliente (nome, email, telefone, dataNascimento) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, 
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                java.sql.Date.valueOf(cliente.getDataNascimento())
            );

            // Depois, obter o ID gerado usando LAST_INSERT_ID()
            String sqlId = "SELECT LAST_INSERT_ID()";
            Integer id = jdbcTemplate.queryForObject(sqlId, Integer.class);
            
            if (id != null) {
                cliente.setId(id);
            } else {
                throw new SQLException("Falha ao obter ID do cliente após inserção");
            }
        } catch (Exception e) {
            log.error("Erro ao salvar cliente: {}", cliente, e);
            throw new SQLException("Erro ao salvar cliente: " + e.getMessage());
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(int idCliente) throws SQLException {
        try {
            String sql = "SELECT * FROM cliente WHERE id_cliente = ?";
            List<Cliente> resultados = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setNome(rs.getString("nome"));
                cliente.setEmail(rs.getString("email"));
                cliente.setTelefone(rs.getString("telefone"));
                java.sql.Date dataNascimento = rs.getDate("dataNascimento");
                if (dataNascimento != null) {
                    cliente.setDataNascimento(dataNascimento.toLocalDate());
                }
                return cliente;
            }, idCliente);

            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
        } catch (Exception e) {
            log.error("Erro ao buscar cliente por ID: {}", idCliente, e);
            throw new SQLException("Erro ao buscar cliente: " + e.getMessage());
        }
    }

    @Override
    public List<Cliente> buscarTodos() throws SQLException {
        try {
            String sql = "SELECT * FROM cliente";
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setNome(rs.getString("nome"));
                cliente.setEmail(rs.getString("email"));
                cliente.setTelefone(rs.getString("telefone"));
                java.sql.Date dataNascimento = rs.getDate("dataNascimento");
                if (dataNascimento != null) {
                    cliente.setDataNascimento(dataNascimento.toLocalDate());
                }
                return cliente;
            });
        } catch (Exception e) {
            log.error("Erro ao buscar todos os clientes", e);
            throw new SQLException("Erro ao buscar clientes: " + e.getMessage());
        }
    }

    @Override
    public void atualizar(Cliente cliente) throws SQLException {
        try {
            String sql = "UPDATE cliente SET nome = ?, email = ?, telefone = ?, dataNascimento = ? WHERE id_cliente = ?";
            jdbcTemplate.update(sql, 
                cliente.getNome(), 
                cliente.getEmail(), 
                cliente.getTelefone(),
                cliente.getDataNascimento(),
                cliente.getId()
            );
        } catch (Exception e) {
            log.error("Erro ao atualizar cliente: {}", cliente, e);
            throw new SQLException("Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    @Override
    public void deletar(int idCliente) throws SQLException {
        try {
            String sql = "DELETE FROM cliente WHERE id_cliente = ?";
            jdbcTemplate.update(sql, idCliente);
        } catch (Exception e) {
            log.error("Erro ao deletar cliente: {}", idCliente, e);
            throw new SQLException("Erro ao deletar cliente: " + e.getMessage());
        }
    }

    @Override
    public Optional<Cliente> buscarPorEmail(String email) throws BusinessRuleException {
        try {
            String sql = "SELECT * FROM cliente WHERE email = ?";
            List<Cliente> resultados = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                cliente.setNome(rs.getString("nome"));
                cliente.setEmail(rs.getString("email"));
                cliente.setTelefone(rs.getString("telefone"));
                java.sql.Date dataNascimento = rs.getDate("dataNascimento");
                if (dataNascimento != null) {
                    cliente.setDataNascimento(dataNascimento.toLocalDate());
                }
                return cliente;
            }, email);

            // Retorna o primeiro resultado se existir, ou Optional.empty() se não existir
            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
        } catch (Exception e) {
            log.error("Erro ao buscar cliente por email: {}", email, e);
            throw new BusinessRuleException("Erro ao buscar cliente: " + e.getMessage());
        }
    }
}