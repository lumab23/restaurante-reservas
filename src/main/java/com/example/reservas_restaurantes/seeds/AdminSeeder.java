package com.example.reservas_restaurantes.seeds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.model.Admin;
import com.example.reservas_restaurantes.repository.AdminRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Component
@Configuration
public class AdminSeeder {

    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private DataSource dataSource;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static boolean jaExecutou = false; 

    public AdminSeeder() {
        System.out.println("AdminSeeder está sendo instanciado!");
    }

    @EventListener(ContextRefreshedEvent.class)
    public void seedAdmins() {
        if (jaExecutou) {
            System.out.println("AdminSeeder já foi executado, pulando...");
            return;
        }
        
        System.out.println("=== INÍCIO DO SEEDING DE ADMINS (VERSÃO CORRIGIDA) ===");
        
        try {
            // Verificar se existem admins
            List<Admin> admins = adminRepository.buscarTodos();
            System.out.println("Admins encontrados: " + admins.size());
            
            // SEMPRE recriar para garantir que as senhas estejam corretas
            System.out.println("Limpando e recriando admins para corrigir senhas...");
            
            recriarAdminsComSenhasCorretas();
            
            jaExecutou = true;
            
        } catch (Exception e) {
            System.err.println("Erro durante o seeding: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("=== FIM DO SEEDING DE ADMINS ===");
    }
    
    private void recriarAdminsComSenhasCorretas() throws Exception {
        // 1. Limpar tabela
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM admin");
            System.out.println("✓ Tabela admin limpa");
        }
        
        // 2. Inserir com senhas corretas
        String sql = "INSERT INTO admin (nome, email, senha, cargo, ativo) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Admin Principal
            inserirAdmin(stmt, "Administrador Principal", "admin@restaurante.com", "admin123", "Gerente");
            
            // Supervisor  
            inserirAdmin(stmt, "Supervisor", "supervisor@restaurante.com", "super123", "Supervisor");
            
            // Atendente
            inserirAdmin(stmt, "Atendente", "atendente@restaurante.com", "aten123", "Atendente");
        }
        
        // 3. Testar imediatamente
        testarCredenciais();
    }
    
    private void inserirAdmin(PreparedStatement stmt, String nome, String email, String senhaTextoPlano, String cargo) throws Exception {
        String senhaCriptografada = passwordEncoder.encode(senhaTextoPlano);
        
        System.out.println("Inserindo: " + nome);
        System.out.println("  Email: " + email);
        System.out.println("  Senha: " + senhaTextoPlano + " -> " + senhaCriptografada.substring(0, 20) + "...");
        
        stmt.setString(1, nome);
        stmt.setString(2, email);
        stmt.setString(3, senhaCriptografada);
        stmt.setString(4, cargo);
        stmt.setBoolean(5, true);
        
        stmt.executeUpdate();
        System.out.println("✓ " + nome + " criado com sucesso!");
    }
    
    private void testarCredenciais() {
        System.out.println("\n=== TESTANDO CREDENCIAIS ===");
        
        String[][] testes = {
            {"admin@restaurante.com", "admin123"},
            {"supervisor@restaurante.com", "super123"},
            {"atendente@restaurante.com", "aten123"}
        };
        
        for (String[] teste : testes) {
            try {
                boolean resultado = adminRepository.autenticar(teste[0], teste[1]);
                System.out.println("Teste " + teste[0] + " / " + teste[1] + ": " + 
                                 (resultado ? "✅ SUCESSO" : "❌ FALHOU"));
            } catch (Exception e) {
                System.err.println("Erro ao testar " + teste[0] + ": " + e.getMessage());
            }
        }
        
        System.out.println("=== FIM DOS TESTES ===\n");
    }
}