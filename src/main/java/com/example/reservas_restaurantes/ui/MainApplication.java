package com.example.reservas_restaurantes.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import com.example.reservas_restaurantes.ReservasRestaurantesApplication;
import org.springframework.boot.SpringApplication;

public class MainApplication extends Application {
    
    private static ConfigurableApplicationContext springContext;
    private static String[] savedArgs;
    
    public static void main(String[] args) {
        savedArgs = args;
        launch(args);
    }
    
    @Override
    public void init() throws Exception {
        try {
            System.out.println("Iniciando contexto Spring...");
            springContext = SpringApplication.run(ReservasRestaurantesApplication.class, savedArgs);
            System.out.println("Contexto Spring iniciado com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar contexto Spring:");
            e.printStackTrace();
            Platform.exit();
            throw e;
        }
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            System.out.println("Iniciando interface JavaFX...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
            loader.setControllerFactory(springContext::getBean);
            
            Scene scene = new Scene(loader.load(), 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            primaryStage.setTitle("Sistema de Reservas - Restaurante");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            
            try {
                primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
            } catch (Exception e) {
                System.out.println("Ícone não encontrado, continuando sem ele");
            }
            
            primaryStage.show();
            System.out.println("Interface JavaFX iniciada com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar interface JavaFX:");
            e.printStackTrace();
            Platform.exit();
            throw e;
        }
    }
    
    @Override
    public void stop() throws Exception {
        if (springContext != null) {
            springContext.close();
        }
    }
}