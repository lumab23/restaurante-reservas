package com.example.reservas_restaurantes.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.reservas_restaurantes.service.AdminService;
import com.example.reservas_restaurantes.model.Admin;
import com.example.reservas_restaurantes.utils.WindowUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

@Component
public class MainController {
    
    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    
    @FXML private VBox mainContainer;
    @FXML private Button btnNovaReserva;
    @FXML private Button btnAcessarReserva;
    @FXML private Hyperlink btnAdminLogin;
    @FXML private Label lblTitulo;
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private ApplicationContext springContext;
    
    private boolean isAdminMode = false;
    
    @FXML
    private void initialize() {
        setupUI();
        verificarAdminsAtivos();
    }
    
    private void setupUI() {
        lblTitulo.setText("Sistema de Reservas - Restaurante Premium");
        btnNovaReserva.setOnAction(e -> abrirNovaReserva());
        btnAdminLogin.setOnAction(e -> abrirLoginAdmin());
    }
    
    private void verificarAdminsAtivos() {
        try {
            List<Admin> admins = adminService.listarTodosAdmins();
            boolean temAdminsAtivos = admins.stream().anyMatch(Admin::isAtivo);
            btnAdminLogin.setVisible(temAdminsAtivos && !isAdminMode);
        } catch (Exception e) {
            log.error("Erro ao verificar admins ativos", e);
            btnAdminLogin.setVisible(false);
        }
    }
    
    @FXML
    private void abrirNovaReserva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cliente-reserva.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent clienteView = loader.load();
            
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
            Parent loginView = loader.load();
            
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
            log.info("Tentando abrir dashboard admin...");
            URL fxmlUrl = getClass().getResource("/fxml/admin-dashboard.fxml");
            if (fxmlUrl == null) {
                log.error("Não foi possível encontrar o arquivo admin-dashboard.fxml");
                mostrarAlerta("Erro", "Arquivo do dashboard não encontrado", Alert.AlertType.ERROR);
                return;
            }
            log.info("URL do FXML: {}", fxmlUrl);
            
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setControllerFactory(springContext::getBean);
            Parent dashboardView = loader.load();
            
            AdminDashboardController controller = loader.getController();
            controller.setMainController(this);
            
            mainContainer.getChildren().clear();
            mainContainer.getChildren().add(dashboardView);
            
            isAdminMode = true;
            log.info("Dashboard admin carregado com sucesso");
            
        } catch (IOException e) {
            log.error("Erro ao abrir dashboard: {}", e.getMessage(), e);
            mostrarAlerta("Erro", "Não foi possível abrir o dashboard: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    public void voltarInicio() {
        try {
            // Em vez de carregar uma nova instância, vamos apenas limpar o container e adicionar a view principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            // Obter a instância do MainController da view carregada
            MainController newController = loader.getController();
            
            // Copiar o estado do isAdminMode para a nova instância
            newController.setAdminMode(this.isAdminMode);
            
            // Configurar a cena
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            // Obter o Stage atual de forma segura
            Stage stage;
            if (mainContainer != null && mainContainer.getScene() != null) {
                stage = (Stage) mainContainer.getScene().getWindow();
            } else {
                // Se não conseguir obter do mainContainer, tentar obter de qualquer outro componente visível
                stage = (Stage) scene.getWindow();
                if (stage == null) {
                    // Se ainda não conseguir, criar um novo Stage
                    stage = new Stage();
                }
            }
            
            stage.setScene(scene);
            stage.setTitle("Sistema de Reservas");
            WindowUtils.configureWindowSize(stage);
            stage.show();
            
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

    @FXML
    private void onAcessarReserva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cliente-acesso.fxml"));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            
            ClienteAcessoController controller = loader.getController();
            controller.setMainController(this);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) btnAcessarReserva.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Acessar Minhas Reservas");
            WindowUtils.configureWindowSize(stage);
            stage.show();
        } catch (IOException e) {
            log.error("Erro ao carregar tela de acesso às reservas", e);
            mostrarAlerta("Erro", "Não foi possível abrir a tela de acesso às reservas.", Alert.AlertType.ERROR);
        }
    }

    public void setAdminMode(boolean adminMode) {
        this.isAdminMode = adminMode;
        verificarAdminsAtivos();
    }
}