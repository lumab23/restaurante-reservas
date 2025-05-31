package com.example.reservas_restaurantes.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.service.AdminService;

import java.io.IOException;

@Component
public class MainController {
    
    @FXML private VBox mainContainer;
    @FXML private Button btnNovaReserva;
    @FXML private Button btnAdminLogin;
    @FXML private Label lblTitulo;
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private ApplicationContext springContext;
    
    private boolean isAdminMode = false;
    
    @FXML
    private void initialize() {
        setupUI();
    }
    
    private void setupUI() {
        lblTitulo.setText("Sistema de Reservas - Restaurante Premium");
        btnNovaReserva.setOnAction(e -> abrirNovaReserva());
        btnAdminLogin.setOnAction(e -> abrirLoginAdmin());
    }
    
    @FXML
    private void abrirNovaReserva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cliente-reserva.fxml"));
            loader.setControllerFactory(springContext::getBean);
            VBox clienteView = loader.load();
            
            ClienteReservaController controller = loader.getController();
            controller.setMainController(this);
            
            // Substituir o conteúdo principal
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(clienteView);
            
        } catch (IOException e) {
            mostrarAlerta("Erro", "Não foi possível abrir a tela de reserva: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void abrirLoginAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin-login.fxml"));
            loader.setControllerFactory(springContext::getBean);
            VBox loginView = loader.load();
            
            AdminLoginController controller = loader.getController();
            controller.setMainController(this);
            
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(loginView);
            
            mainContainer.setVisible(true);
            
        } catch (IOException e) {
            mostrarAlerta("Erro", "Não foi possível abrir a tela de login: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    public void abrirDashboardAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin-dashboard.fxml"));
            loader.setControllerFactory(springContext::getBean);
            VBox dashboardView = loader.load();
            
            AdminDashboardController controller = loader.getController();
            controller.setMainController(this);
            
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(dashboardView);
            
            isAdminMode = true;
            
        } catch (IOException e) {
            mostrarAlerta("Erro", "Não foi possível abrir o dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    public void voltarInicio() {
        try {
            // Em vez de recarregar a view principal, vamos apenas limpar o container
            // e adicionar os elementos da view principal diretamente
            mainContainer.getChildren().clear();
            
            // Recriar os elementos da view principal
            Label titulo = new Label("Sistema de Reservas - Restaurante Premium");
            titulo.getStyleClass().add("title-label");
            
            Button btnNovaReserva = new Button("Nova Reserva");
            btnNovaReserva.getStyleClass().add("primary-button");
            btnNovaReserva.setOnAction(e -> abrirNovaReserva());
            
            Button btnAdminLogin = new Button("Login Administrativo");
            btnAdminLogin.getStyleClass().add("secondary-button");
            btnAdminLogin.setOnAction(e -> abrirLoginAdmin());
            
            // Adicionar os elementos ao container
            mainContainer.getChildren().addAll(titulo, btnNovaReserva, btnAdminLogin);
            
            // Aplicar estilos
            mainContainer.setSpacing(20);
            mainContainer.setAlignment(javafx.geometry.Pos.CENTER);
            
            isAdminMode = false;
            
        } catch (Exception e) {
            mostrarAlerta("Erro", "Não foi possível voltar ao início: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    public void setMainContainer(VBox container) {
        this.mainContainer = container;
    }
    
    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
    
    public ApplicationContext getSpringContext() {
        return springContext;
    }
}