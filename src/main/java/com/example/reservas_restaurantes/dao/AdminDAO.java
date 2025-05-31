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
            
            // Criptografar a senha antes de salvar
            String senhaCriptografada = passwordEncoder.encode(admin.getSenha());
            System.out.println("Salvando admin com senha original: " + admin.getSenha());
            System.out.println("Senha criptografada: " + senhaCriptografada);
            
            stmt.setString(1, admin.getNome());
            stmt.setString(2, admin.getEmail());
            stmt.setString(3, senhaCriptografada);
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
        String sql = "SELECT * FROM admin WHERE email = ? AND ativo = true";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = mapAdmin(rs);
                    System.out.println("Admin encontrado no banco: " + admin.getNome() + " - Email: " + admin.getEmail());
                    return Optional.of(admin);
                }
            }
        }
        System.out.println("Nenhum admin encontrado com email: " + email);
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
            
            // Criptografar a senha se ela foi alterada
            String senhaCriptografada = admin.getSenha().startsWith("$2a$") ? 
                admin.getSenha() : passwordEncoder.encode(admin.getSenha());
            
            stmt.setString(1, admin.getNome());
            stmt.setString(2, admin.getEmail());
            stmt.setString(3, senhaCriptografada);
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
        System.out.println("=== INÍCIO DA AUTENTICAÇÃO ===");
        System.out.println("Email fornecido: '" + email + "'");
        System.out.println("Senha fornecida: '" + senha + "'");
        
        // Primeiro, vamos verificar se o admin existe
        Optional<Admin> adminOpt = buscarPorEmail(email);
        
        if (adminOpt.isEmpty()) {
            System.out.println("ERRO: Admin não encontrado com email: " + email);
            
            // Debug: Vamos listar todos os emails no banco
            System.out.println("Listando todos os admins no banco:");
            List<Admin> todosAdmins = buscarTodos();
            for (Admin admin : todosAdmins) {
                System.out.println("- Email: '" + admin.getEmail() + "' | Nome: " + admin.getNome() + " | Ativo: " + admin.isAtivo());
            }
            return false;
        }
        
        Admin admin = adminOpt.get();
        System.out.println("Admin encontrado: " + admin.getNome());
        System.out.println("Senha hash no banco: " + admin.getSenha());
        
        // Verificar se o admin está ativo
        if (!admin.isAtivo()) {
            System.out.println("ERRO: Admin está inativo");
            return false;
        }
        
        // Comparar as senhas
        boolean senhaCorreta = passwordEncoder.matches(senha, admin.getSenha());
        System.out.println("Resultado da comparação de senha: " + senhaCorreta);
        
        // Debug adicional
        if (!senhaCorreta) {
            System.out.println("TESTE: Gerando novo hash para a senha fornecida:");
            String novoHash = passwordEncoder.encode(senha);
            System.out.println("Novo hash: " + novoHash);
            System.out.println("Teste com novo hash: " + passwordEncoder.matches(senha, novoHash));
        }
        
        System.out.println("=== FIM DA AUTENTICAÇÃO ===");
        return senhaCorreta;
    }

    private Admin mapAdmin(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setIdAdmin(rs.getInt("id_admin"));
        admin.setNome(rs.getString("nome"));
        admin.setEmail(rs.getString("email"));
        admin.setSenha(rs.getString("senha")); // Mantém o hash original
        admin.setCargo(rs.getString("cargo"));
        admin.setAtivo(rs.getBoolean("ativo"));
        return admin;
    }
}