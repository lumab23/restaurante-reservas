package com.example.reservas_restaurantes.utils;

import javafx.stage.Stage;

public class WindowUtils {
    // Tamanhos padrão para todas as janelas do sistema
    public static final double WINDOW_WIDTH = 1000;
    public static final double WINDOW_HEIGHT = 700;
    public static final double MIN_WINDOW_WIDTH = 800;
    public static final double MIN_WINDOW_HEIGHT = 600;

    /**
     * Configura o tamanho padrão para uma janela do sistema.
     * @param stage A janela a ser configurada
     */
    public static void configureWindowSize(Stage stage) {
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.setMinWidth(MIN_WINDOW_WIDTH);
        stage.setMinHeight(MIN_WINDOW_HEIGHT);
    }
} 