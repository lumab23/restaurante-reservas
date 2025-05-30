package com.example.reservas_restaurantes.seeds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.service.AdminService;
import com.example.reservas_restaurantes.model.Admin;
import java.util.List;

@Component
@Configuration
public class AdminSeeder {

    @Autowired
    private AdminService adminService;

    public AdminSeeder() {
        System.out.println("AdminSeeder está sendo instanciado!");
    }

    @EventListener(ContextRefreshedEvent.class)
    public void seedAdmins() {
        System.out.println("Evento ContextRefreshedEvent recebido!");
        try {
            System.out.println("Verificando se existem admins no banco...");
            List<Admin> admins = adminService.listarTodosAdmins();
            System.out.println("Número de admins encontrados: " + admins.size());
            
            if (admins.isEmpty()) {
                System.out.println("Criando admins iniciais...");
                // Cria o administrador principal
                Admin admin = adminService.cadastrarAdmin(
                    "Administrador Principal",
                    "admin@restaurante.com",
                    "admin123",
                    "Gerente"
                );
                System.out.println("Admin principal criado com ID: " + admin.getIdAdmin());

                // Cria o supervisor
                Admin supervisor = adminService.cadastrarAdmin(
                    "Supervisor",
                    "supervisor@restaurante.com",
                    "super123",
                    "Supervisor"
                );
                System.out.println("Supervisor criado com ID: " + supervisor.getIdAdmin());

                // Cria o atendente
                Admin atendente = adminService.cadastrarAdmin(
                    "Atendente",
                    "atendente@restaurante.com",
                    "aten123",
                    "Atendente"
                );
                System.out.println("Atendente criado com ID: " + atendente.getIdAdmin());

                System.out.println("Administradores iniciais criados com sucesso!");
            } else {
                System.out.println("Admins já existem no banco:");
                for (Admin admin : admins) {
                    System.out.println("- " + admin.getNome() + " (" + admin.getEmail() + ")");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar administradores iniciais: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 