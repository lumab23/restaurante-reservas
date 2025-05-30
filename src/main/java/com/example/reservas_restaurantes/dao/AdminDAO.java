package com.example.reservas_restaurantes.dao;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.reservas_restaurantes.model.Admin;
import com.example.reservas_restaurantes.repository.AdminRepository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AdminDAO implements AdminRepository {

    private final DataSource dataSource;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminDAO(DataSource dataSource) {
        this.dataSource = dataSource;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    private Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public void salvar(Admin admin) throws SQLException {
        String sql = "INSERT INTO admin (nome, email, senha, cargo, ativo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, admin.getNome());
            stmt.setString(2, admin.getEmail());
            stmt.setString(3, passwordEncoder.encode(admin.getSenha()));
            stmt.setString(4, admin.getCargo());
            stmt.setBoolean(5, admin.isAtivo());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    admin.setIdAdmin(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Optional<Admin> buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM admin WHERE id_admin = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAdmin(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Admin> buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM admin WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAdmin(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Admin> buscarTodos() throws SQLException {
        String sql = "SELECT * FROM admin";
        List<Admin> admins = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                admins.add(mapAdmin(rs));
            }
        }
        return admins;
    }

    @Override
    public void atualizar(Admin admin) throws SQLException {
        String sql = "UPDATE admin SET nome = ?, email = ?, senha = ?, cargo = ?, ativo = ? WHERE id_admin = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, admin.getNome());
            stmt.setString(2, admin.getEmail());
            stmt.setString(3, passwordEncoder.encode(admin.getSenha()));
            stmt.setString(4, admin.getCargo());
            stmt.setBoolean(5, admin.isAtivo());
            stmt.setInt(6, admin.getIdAdmin());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM admin WHERE id_admin = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public boolean autenticar(String email, String senha) throws SQLException {
        System.out.println("Tentando autenticar admin com email: " + email);
        Optional<Admin> adminOpt = buscarPorEmail(email);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            System.out.println("Admin encontrado: " + admin.getNome());
            System.out.println("Senha fornecida: " + senha);
            System.out.println("Senha armazenada (hash): " + admin.getSenha());
            boolean matches = passwordEncoder.matches(senha, admin.getSenha());
            System.out.println("Resultado da comparação: " + matches);
            return matches;
        }
        System.out.println("Admin não encontrado com email: " + email);
        return false;
    }

    private Admin mapAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setIdAdmin(rs.getInt("id_admin"));
        admin.setNome(rs.getString("nome"));
        admin.setEmail(rs.getString("email"));
        admin.setSenha(rs.getString("senha"));
        admin.setCargo(rs.getString("cargo"));
        admin.setAtivo(rs.getBoolean("ativo"));
        return admin;
    }
} 