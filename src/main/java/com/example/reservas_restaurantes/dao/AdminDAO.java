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

@Repository // Anotação que marca esta classe como um componente de acesso a dados
public class AdminDAO implements AdminRepository {

    private final DataSource dataSource; // Fonte de dados para conexão com o banco
    private final BCryptPasswordEncoder passwordEncoder; // Codificador de senha para criptografia

    public AdminDAO(DataSource dataSource) { // Construtor que recebe a fonte de dados
        this.dataSource = dataSource;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Inicializa o codificador de senha
    }

    private Connection getConnection() throws SQLException { // Método para obter conexão com o banco
        return DataSourceUtils.getConnection(dataSource);
    }

    @Override
    public void salvar(Admin admin) throws SQLException { // Método para salvar um novo administrador no banco
        String sql = "INSERT INTO admin (nome, email, senha, cargo, ativo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            String senhaCriptografada = passwordEncoder.encode(admin.getSenha()); // Criptografa a senha antes de salvar
            System.out.println("Salvando admin com senha original: " + admin.getSenha());
            System.out.println("Senha criptografada: " + senhaCriptografada);
            
            stmt.setString(1, admin.getNome()); // Define o nome do admin
            stmt.setString(2, admin.getEmail()); // Define o email do admin
            stmt.setString(3, senhaCriptografada); // Define a senha criptografada
            stmt.setString(4, admin.getCargo()); // Define o cargo do admin
            stmt.setBoolean(5, admin.isAtivo()); // Define se o admin está ativo
            stmt.executeUpdate(); // Executa a inserção no banco

            try (ResultSet rs = stmt.getGeneratedKeys()) { // Obtém o ID gerado
                if (rs.next()) {
                    admin.setId(rs.getInt(1)); // Define o ID do admin
                }
            }
        }
    }

    @Override
    public Optional<Admin> buscarPorId(int id) throws SQLException { // Método para buscar admin pelo ID
        String sql = "SELECT * FROM admin WHERE id_admin = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id); // Define o ID na consulta
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapAdmin(rs)); // Mapeia o resultado para objeto Admin
                }
            }
        }
        return Optional.empty(); // Retorna vazio se não encontrar
    }

    @Override
    public Optional<Admin> buscarPorEmail(String email) throws SQLException { // Método para buscar admin pelo email
        String sql = "SELECT * FROM admin WHERE email = ? AND ativo = true";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email); // Define o email na consulta
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = mapAdmin(rs); // Mapeia o resultado para objeto Admin
                    System.out.println("Admin encontrado no banco: " + admin.getNome() + " - Email: " + admin.getEmail());
                    return Optional.of(admin);
                }
            }
        }
        System.out.println("Nenhum admin encontrado com email: " + email);
        return Optional.empty(); // Retorna vazio se não encontrar
    }

    @Override
    public List<Admin> buscarTodos() throws SQLException { // Método para buscar todos os admins
        String sql = "SELECT * FROM admin";
        List<Admin> admins = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                admins.add(mapAdmin(rs)); // Adiciona cada admin encontrado à lista
            }
        }
        return admins;
    }

    @Override
    public void atualizar(Admin admin) throws SQLException { // Método para atualizar dados do admin
        String sql = "UPDATE admin SET nome = ?, email = ?, senha = ?, cargo = ?, ativo = ? WHERE id_admin = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String senhaCriptografada = admin.getSenha().startsWith("$2a$") ? 
                admin.getSenha() : passwordEncoder.encode(admin.getSenha()); // Criptografa a senha se foi alterada
            
            stmt.setString(1, admin.getNome()); // Define o novo nome
            stmt.setString(2, admin.getEmail()); // Define o novo email
            stmt.setString(3, senhaCriptografada); // Define a nova senha
            stmt.setString(4, admin.getCargo()); // Define o novo cargo
            stmt.setBoolean(5, admin.isAtivo()); // Define o novo status
            stmt.setInt(6, admin.getId()); // Define o ID do admin a ser atualizado
            stmt.executeUpdate(); // Executa a atualização
        }
    }

    @Override
    public void deletar(int id) throws SQLException { // Método para deletar um admin
        String sql = "DELETE FROM admin WHERE id_admin = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id); // Define o ID do admin a ser deletado
            stmt.executeUpdate(); // Executa a deleção
        }
    }

    @Override
    public boolean autenticar(String email, String senha) throws SQLException { // Método para autenticar admin
        System.out.println("=== INÍCIO DA AUTENTICAÇÃO ===");
        System.out.println("Email fornecido: '" + email + "'");
        System.out.println("Senha fornecida: '" + senha + "'");
        
        Optional<Admin> adminOpt = buscarPorEmail(email); // Busca admin pelo email
        
        if (adminOpt.isEmpty()) { // Verifica se o admin existe
            System.out.println("ERRO: Admin não encontrado com email: " + email);
            
            System.out.println("Listando todos os admins no banco:"); // Debug: Lista todos os admins
            List<Admin> todosAdmins = buscarTodos();
            for (Admin admin : todosAdmins) {
                System.out.println("- Email: '" + admin.getEmail() + "' | Nome: " + admin.getNome() + " | Ativo: " + admin.isAtivo());
            }
            return false;
        }
        
        Admin admin = adminOpt.get();
        System.out.println("Admin encontrado: " + admin.getNome());
        System.out.println("Senha hash no banco: " + admin.getSenha());
        
        if (!admin.isAtivo()) { // Verifica se o admin está ativo
            System.out.println("ERRO: Admin está inativo");
            return false;
        }
        
        boolean senhaCorreta = passwordEncoder.matches(senha, admin.getSenha()); // Verifica se a senha está correta
        System.out.println("Resultado da comparação de senha: " + senhaCorreta);
        
        if (!senhaCorreta) { // Debug adicional para problemas de senha
            System.out.println("TESTE: Gerando novo hash para a senha fornecida:");
            String novoHash = passwordEncoder.encode(senha);
            System.out.println("Novo hash: " + novoHash);
            System.out.println("Teste com novo hash: " + passwordEncoder.matches(senha, novoHash));
        }
        
        System.out.println("=== FIM DA AUTENTICAÇÃO ===");
        return senhaCorreta;
    }

    private Admin mapAdmin(ResultSet rs) throws SQLException { // Método para mapear resultado do banco para objeto Admin
        Admin admin = new Admin();
        admin.setId(rs.getInt("id_admin")); // Define o ID
        admin.setNome(rs.getString("nome")); // Define o nome
        admin.setEmail(rs.getString("email")); // Define o email
        admin.setSenha(rs.getString("senha")); // Define a senha
        admin.setCargo(rs.getString("cargo")); // Define o cargo
        admin.setAtivo(rs.getBoolean("ativo")); // Define o status
        return admin;
    }
}