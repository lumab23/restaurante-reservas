package com.example.reservas_restaurantes.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.service.AdminService;
import com.example.reservas_restaurantes.utils.WindowUtils;

import java.io.IOException;

@Component
public class AdminLoginController {
    
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtSenha;
    @FXML private Button btnLogin;
    @FXML private Button btnVoltar;
    @FXML private Label lblErro;
    
    @Autowired
    private AdminService adminService;
    
    private MainController mainController;
    
    @FXML
    private void initialize() {
        System.out.println("Inicializando AdminLoginController...");
        btnLogin.setOnAction(e -> realizarLogin());
        btnVoltar.setOnAction(e -> {
            System.out.println("Botão voltar clicado");
            if (mainController != null) {
                System.out.println("MainController encontrado, voltando...");
                voltarInicio();
            } else {
                System.out.println("ERRO: MainController é null!");
                mostrarErro("Erro ao voltar: Controller não inicializado");
            }
        });
        lblErro.setVisible(false);
    }
    
    public void setMainController(MainController mainController) {
        System.out.println("Definindo MainController no AdminLoginController");
        this.mainController = mainController;
        if (this.mainController == null) {
            System.out.println("AVISO: MainController foi definido como null!");
        }
    }
    
    @FXML
    private void realizarLogin() {
        String email = txtEmail.getText().trim();
        String senha = txtSenha.getText();
        
        if (email.isEmpty() || senha.isEmpty()) {
            mostrarErro("Por favor, preencha todos os campos.");
            return;
        }
        
        try {
            if (adminService.autenticar(email, senha)) {
                lblErro.setVisible(false);
                mainController.abrirDashboardAdmin();
            } else {
                mostrarErro("Email ou senha incorretos.");
            }
        } catch (Exception e) {
            mostrarErro("Erro ao realizar login: " + e.getMessage());
        }
    }
    
    @FXML
    private void voltarInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
            loader.setControllerFactory(mainController.getSpringContext()::getBean);
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sistema de Reservas");
            WindowUtils.configureWindowSize(stage);
            stage.show();
        } catch (IOException e) {
            mostrarErro("Erro ao voltar: " + e.getMessage());
        }
    }
    
    private void mostrarErro(String mensagem) {
        lblErro.setText(mensagem);
        lblErro.setVisible(true);
    }
}