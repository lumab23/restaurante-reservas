package com.example.reservas_restaurantes.ui.controller;

import com.example.reservas_restaurantes.exception.BusinessRuleException;
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
    @FXML private Label lblEstatisticas;
    
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
        setupContextMenu();
        carregarDados();
        atualizarEstatisticas();
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
        btnAtualizarReservas.setOnAction(e -> onAtualizarReservas());
        btnAtualizarClientes.setOnAction(e -> carregarClientes());
        btnAtualizarMesas.setOnAction(e -> carregarMesas());
        btnLogout.setOnAction(e -> logout());
    }

    private void setupContextMenu() {
        // Menu para a tabela de Reservas
        ContextMenu reservaContextMenu = new ContextMenu();
        MenuItem deletarReservaItem = new MenuItem("Deletar Reserva");
        deletarReservaItem.setOnAction(event -> deletarReservaSelecionada());
        reservaContextMenu.getItems().add(deletarReservaItem);
        tableReservas.setContextMenu(reservaContextMenu);
    }

    /**
     * Deleta a reserva selecionada na tabela, após confirmação.
     */
    private void deletarReservaSelecionada() {
        Reserva reservaSelecionada = tableReservas.getSelectionModel().getSelectedItem();
        if (reservaSelecionada == null) {
            mostrarAlerta("Nenhuma seleção", "Por favor, selecione uma reserva para deletar.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza que deseja deletar a reserva selecionada?", ButtonType.YES, ButtonType.NO);
        confirmacao.setTitle("Confirmar Deleção");
        confirmacao.setHeaderText("Deletar Reserva ID: " + reservaSelecionada.getIdReserva());

        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    reservaService.deletarReserva(reservaSelecionada.getIdReserva());
                    mostrarAlerta("Sucesso", "Reserva deletada com sucesso.", Alert.AlertType.INFORMATION);
                    onAtualizarReservas(); // Atualiza tabelas e estatísticas
                } catch (BusinessRuleException e) {
                    mostrarAlerta("Erro", "Falha ao deletar a reserva: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }
    
    private void carregarDados() {
        carregarReservas();
        carregarClientes();
        carregarMesas();
    }
    
    private void carregarReservas() {
        try {
            System.out.println("Iniciando carregamento de reservas...");
            List<Reserva> reservas = reservaService.listarTodasReservas();
            System.out.println("Reservas carregadas: " + reservas.size());
            tableReservas.setItems(FXCollections.observableArrayList(reservas));
        } catch (Exception e) {
            System.err.println("Erro detalhado ao carregar reservas: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao carregar reservas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void carregarClientes() {
        try {
            System.out.println("Iniciando carregamento de clientes...");
            List<Cliente> clientes = clienteService.listarTodosClientes();
            System.out.println("Clientes carregados: " + clientes.size());
            tableClientes.setItems(FXCollections.observableArrayList(clientes));
        } catch (Exception e) {
            System.err.println("Erro detalhado ao carregar clientes: " + e.getMessage());
            e.printStackTrace();
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
    
    private void atualizarEstatisticas() {
        try {
            // Buscar reservas do dia
            var reservasHoje = reservaService.listarReservasPorData(LocalDate.now());
            int totalReservas = reservasHoje.size();
            int reservasConfirmadas = (int) reservasHoje.stream()
                .filter(r -> r.getStatusReserva().name().equals("CONFIRMADA"))
                .count();
            
            // Atualizar label com estatísticas
            lblEstatisticas.setText(String.format(
                "Total de reservas hoje: %d\n" +
                "Reservas confirmadas: %d",
                totalReservas,
                reservasConfirmadas
            ));
            
        } catch (Exception e) {
            System.err.println("Erro ao atualizar estatísticas: " + e.getMessage());
            lblEstatisticas.setText("Erro ao carregar estatísticas");
        }
    }
    
    @FXML
    private void onAtualizarReservas() {
        carregarDados();
        atualizarEstatisticas();
    }
}