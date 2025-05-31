package com.example.reservas_restaurantes.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.repository.AdminRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * Classe para limpar e recriar os dados dos admins corretamente
 * Execute uma vez para corrigir o problema
 */
@Component
@Order(1) // Execute antes do seeder
public class AdminDataFixer implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private AdminRepository adminRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== INICIANDO CORREÇÃO DOS DADOS ADMIN ===");
        
        try {
            // 1. Limpar tabela admin
            limparTabelaAdmin();
            
            // 2. Inserir dados corretos diretamente
            inserirAdminsCorretos();
            
            // 3. Testar autenticação
            testarCredenciais();
            
        } catch (Exception e) {
            System.err.println("Erro durante a correção: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== CORREÇÃO FINALIZADA ===");
    }
    
    private void limparTabelaAdmin() throws Exception {
        System.out.println("Limpando tabela admin...");
        
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate("DELETE FROM admin");
            System.out.println("✓ Tabela admin limpa");
        }
    }
    
    private void inserirAdminsCorretos() throws Exception {
        System.out.println("Inserindo admins com senhas corretas...");
        
        String sql = "INSERT INTO admin (nome, email, senha, cargo, ativo) VALUES (?, ?, ?, ?, ?)";
        
        // Dados dos admins com senhas em texto puro que serão criptografadas
        String[][] adminsData = {
            {"Administrador Principal", "admin@restaurante.com", "admin123", "Gerente"},
            {"Supervisor", "supervisor@restaurante.com", "super123", "Supervisor"},
            {"Atendente", "atendente@restaurante.com", "aten123", "Atendente"}
        };
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (String[] adminData : adminsData) {
                String nome = adminData[0];
                String email = adminData[1];
                String senhaTextoPlano = adminData[2];
                String cargo = adminData[3];
                
                // Criptografar a senha corretamente
                String senhaCriptografada = passwordEncoder.encode(senhaTextoPlano);
                
                System.out.println("Inserindo: " + nome);
                System.out.println("  Email: " + email);
                System.out.println("  Senha original: " + senhaTextoPlano);
                System.out.println("  Senha criptografada: " + senhaCriptografada);
                
                stmt.setString(1, nome);
                stmt.setString(2, email);
                stmt.setString(3, senhaCriptografada);
                stmt.setString(4, cargo);
                stmt.setBoolean(5, true);
                
                stmt.executeUpdate();
                System.out.println("✓ " + nome + " inserido com sucesso");
            }
        }
    }
    
    private void testarCredenciais() throws Exception {
        System.out.println("\n--- TESTANDO CREDENCIAIS CORRIGIDAS ---");
        
        String[][] credenciais = {
            {"admin@restaurante.com", "admin123"},
            {"supervisor@restaurante.com", "super123"},
            {"atendente@restaurante.com", "aten123"}
        };
        
        for (String[] cred : credenciais) {
            String email = cred[0];
            String senha = cred[1];
            
            System.out.println("\nTestando: " + email + " / " + senha);
            
            try {
                boolean resultado = adminRepository.autenticar(email, senha);
                System.out.println("Resultado: " + (resultado ? "✓ SUCESSO" : "✗ FALHOU"));
                
                if (!resultado) {
                    System.err.println("❌ AINDA TEM PROBLEMA COM: " + email);
                }
            } catch (Exception e) {
                System.err.println("Erro ao testar " + email + ": " + e.getMessage());
            }
        }
        
        System.out.println("--- FIM DOS TESTES ---");
    }
}