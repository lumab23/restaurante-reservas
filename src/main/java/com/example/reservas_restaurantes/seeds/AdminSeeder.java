package com.example.reservas_restaurantes.seeds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import com.example.reservas_restaurantes.service.AdminService;

@Configuration
public class AdminSeeder {

    @Autowired
    private AdminService adminService;

    @EventListener(ContextRefreshedEvent.class)
    public void seedAdmins() {
        try {
            if (adminService.listarTodosAdmins().isEmpty()) {
                // Cria o administrador principal
                adminService.cadastrarAdmin(
                    "Administrador Principal",
                    "admin@restaurante.com",
                    "admin123",
                    "Gerente"
                );

                // Cria o supervisor
                adminService.cadastrarAdmin(
                    "Supervisor",
                    "supervisor@restaurante.com",
                    "super123",
                    "Supervisor"
                );

                // Cria o atendente
                adminService.cadastrarAdmin(
                    "Atendente",
                    "atendente@restaurante.com",
                    "aten123",
                    "Atendente"
                );

                System.out.println("Administradores iniciais criados com sucesso!");
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar administradores iniciais: " + e.getMessage());
        }
    }
} 