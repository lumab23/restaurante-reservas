package com.example.reservas_restaurantes.ui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.model.Cliente;
import com.example.reservas_restaurantes.model.Mesa;
import com.example.reservas_restaurantes.model.Reserva;
import com.example.reservas_restaurantes.service.ClienteService;
import com.example.reservas_restaurantes.service.MesaService;
import com.example.reservas_restaurantes.service.ReservaService;

import java.time.LocalDate;
import java.util.List;

@Component
public class AdminDashboardController {
    
    @FXML private TabPane tabPane;
    
    // Aba Reservas
    @FXML private TableView<Reserva> tableReservas;
    @FXML private TableColumn<Reserva, Integer> colIdReserva;
    @FXML private TableColumn<Reserva, Integer> colIdClienteReserva;
    @FXML private TableColumn<Reserva, Integer> colIdMesaReserva;
    @FXML private TableColumn<Reserva, String> colDataHora;
    @FXML private TableColumn<Reserva, Integer> colNumPessoas;
    @FXML private TableColumn<Reserva, String> colStatus;
    @FXML private DatePicker dpFiltroData;
    @FXML private Button btnFiltrarReservas;
    @FXML private Button btnAtualizarReservas;
    
    // Aba Clientes
    @FXML private TableView<Cliente> tableClientes;
    @FXML private TableColumn<Cliente, Integer> colIdClienteCliente;
    @FXML private TableColumn<Cliente, String> colNomeCliente;
    @FXML private TableColumn<Cliente, String> colEmailCliente;
    @FXML private TableColumn<Cliente, String> colTelefoneCliente;
    @FXML private Button btnAtualizarClientes;
    
    // Aba Mesas
    @FXML private TableView<Mesa> tableMesas;
    @FXML private TableColumn<Mesa, Integer> colIdMesaMesa;
    @FXML private TableColumn<Mesa, Integer> colCapacidade;
    @FXML private TableColumn<Mesa, String> colLocalizacao;
    @FXML private TableColumn<Mesa, String> colStatusMesa;
    @FXML private Button btnAtualizarMesas;
    
    @FXML private Button btnLogout;
    
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private MesaService mesaService;
    
    private MainController mainController;
    
    @FXML
    private void initialize() {
        setupTables();
        setupActions();
        carregarDados();
    }
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    private void setupTables() {
        // Configurar tabela de reservas
        colIdReserva.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        colIdClienteReserva.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colIdMesaReserva.setCellValueFactory(new PropertyValueFactory<>("idMesa"));
        colDataHora.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> 
                cellData.getValue().getDataHora().toString()));
        colNumPessoas.setCellValueFactory(new PropertyValueFactory<>("numPessoas"));
        colStatus.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> 
                cellData.getValue().getStatusReserva().toString()));
        
        // Configurar tabela de clientes
        colIdClienteCliente.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colNomeCliente.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmailCliente.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelefoneCliente.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        
        // Configurar tabela de mesas
        colIdMesaMesa.setCellValueFactory(new PropertyValueFactory<>("idMesa"));
        colCapacidade.setCellValueFactory(new PropertyValueFactory<>("capacidade"));
        colLocalizacao.setCellValueFactory(new PropertyValueFactory<>("localizacao"));
        colStatusMesa.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> 
                cellData.getValue().getStatusMesa().toString()));
    }
    
    private void setupActions() {
        btnFiltrarReservas.setOnAction(e -> filtrarReservasPorData());
        btnAtualizarReservas.setOnAction(e -> carregarReservas());
        btnAtualizarClientes.setOnAction(e -> carregarClientes());
        btnAtualizarMesas.setOnAction(e -> carregarMesas());
        btnLogout.setOnAction(e -> logout());
    }
    
    private void carregarDados() {
        carregarReservas();
        carregarClientes();
        carregarMesas();
    }
    
    private void carregarReservas() {
        try {
            List<Reserva> reservas = reservaService.listarTodasReservas();
            tableReservas.setItems(FXCollections.observableArrayList(reservas));
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao carregar reservas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void carregarClientes() {
        try {
            List<Cliente> clientes = clienteService.listarTodosClientes();
            tableClientes.setItems(FXCollections.observableArrayList(clientes));
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao carregar clientes: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void carregarMesas() {
        try {
            List<Mesa> mesas = mesaService.listarTodasMesas();
            tableMesas.setItems(FXCollections.observableArrayList(mesas));
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao carregar mesas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void filtrarReservasPorData() {
        LocalDate dataFiltro = dpFiltroData.getValue();
        if (dataFiltro == null) {
            carregarReservas();
            return;
        }
        
        try {
            List<Reserva> reservas = reservaService.listarReservasPorData(dataFiltro);
            tableReservas.setItems(FXCollections.observableArrayList(reservas));
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao filtrar reservas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void logout() {
        mainController.voltarInicio();
    }
    
    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}