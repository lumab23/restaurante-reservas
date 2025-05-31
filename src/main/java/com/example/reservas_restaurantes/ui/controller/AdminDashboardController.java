package com.example.reservas_restaurantes.ui.controller;

import com.example.reservas_restaurantes.exception.BusinessRuleException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.application.Platform;

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
        setupTableListeners();
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

        // Menu para a tabela de Clientes
        ContextMenu clienteContextMenu = new ContextMenu();
        MenuItem deletarClienteItem = new MenuItem("Deletar Cliente");
        deletarClienteItem.setOnAction(event -> deletarClienteSelecionado());
        clienteContextMenu.getItems().add(deletarClienteItem);
        tableClientes.setContextMenu(clienteContextMenu);
    }

    private void setupTableListeners() {
        // Listener para atualizar a tabela de clientes quando mudar de aba
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null && newTab.getText().equals("Clientes")) {
                carregarClientes();
            }
        });

        // Listener para atualizar a tabela de clientes quando a tabela de reservas for atualizada
        tableReservas.getItems().addListener((javafx.collections.ListChangeListener.Change<? extends Reserva> change) -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    carregarClientes();
                }
            }
        });
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
                    // Guardar o ID do cliente antes de deletar a reserva
                    int idCliente = reservaSelecionada.getIdCliente();
                    
                    // Deletar a reserva
                    reservaService.deletarReserva(reservaSelecionada.getIdReserva());
                    mostrarAlerta("Sucesso", "Reserva deletada com sucesso.", Alert.AlertType.INFORMATION);
                    
                    // Atualizar as tabelas
                    carregarReservas();
                    carregarClientes();
                    atualizarEstatisticas();
                    
                    // Verificar se o cliente ainda existe após a deleção
                    try {
                        var clienteOpt = clienteService.buscarClientePorId(idCliente);
                        if (clienteOpt == null) {
                            System.out.println("Cliente ID " + idCliente + " foi deletado junto com a reserva");
                            // Forçar atualização da tabela de clientes
                            tableClientes.refresh();
                        }
                    } catch (Exception e) {
                        // Se o cliente não for encontrado, significa que foi deletado
                        System.out.println("Cliente ID " + idCliente + " não encontrado após deleção da reserva");
                        tableClientes.refresh();
                    }
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
            
            // Atualizar a tabela de clientes
            tableClientes.setItems(FXCollections.observableArrayList(clientes));
            tableClientes.refresh(); // Forçar atualização visual
            
            // Se estiver na aba de clientes, garantir que a tabela seja atualizada
            if (tabPane.getSelectionModel().getSelectedIndex() == 1) {
                Platform.runLater(() -> tableClientes.refresh());
            }
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

    /**
     * Deleta o cliente selecionado na tabela, após confirmação.
     * Verifica se o cliente tem reservas ativas antes de deletar.
     */
    private void deletarClienteSelecionado() {
        Cliente clienteSelecionado = tableClientes.getSelectionModel().getSelectedItem();
        if (clienteSelecionado == null) {
            mostrarAlerta("Nenhuma seleção", "Por favor, selecione um cliente para deletar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Verificar se o cliente tem reservas ativas
            List<Reserva> reservasCliente = reservaService.listarReservasPorCliente(clienteSelecionado.getIdCliente());
            if (!reservasCliente.isEmpty()) {
                StringBuilder mensagem = new StringBuilder();
                mensagem.append("Este cliente possui ").append(reservasCliente.size()).append(" reserva(s):\n\n");
                
                for (Reserva reserva : reservasCliente) {
                    mensagem.append("Reserva ID: ").append(reserva.getIdReserva())
                           .append(" - Data: ").append(reserva.getDataHora())
                           .append(" - Status: ").append(reserva.getStatusReserva())
                           .append("\n");
                }
                
                mensagem.append("\nDeseja deletar o cliente e todas as suas reservas?");
                
                Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, mensagem.toString(), ButtonType.YES, ButtonType.NO);
                confirmacao.setTitle("Confirmar Deleção");
                confirmacao.setHeaderText("Cliente possui reservas ativas");
                
                confirmacao.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        deletarClienteEReservas(clienteSelecionado, reservasCliente);
                    }
                });
            } else {
                // Se não tiver reservas, apenas confirma a deleção
                Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, 
                    "Tem certeza que deseja deletar o cliente selecionado?", 
                    ButtonType.YES, ButtonType.NO);
                confirmacao.setTitle("Confirmar Deleção");
                confirmacao.setHeaderText("Deletar Cliente: " + clienteSelecionado.getNome());
                
                confirmacao.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        deletarClienteEReservas(clienteSelecionado, List.of());
                    }
                });
            }
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao verificar reservas do cliente: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Deleta o cliente e suas reservas (se houver).
     */
    private void deletarClienteEReservas(Cliente cliente, List<Reserva> reservas) {
        try {
            // Primeiro deleta todas as reservas do cliente
            for (Reserva reserva : reservas) {
                try {
                    reservaService.deletarReserva(reserva.getIdReserva());
                    System.out.println("Reserva ID " + reserva.getIdReserva() + " deletada com sucesso");
                } catch (Exception e) {
                    System.err.println("Erro ao deletar reserva ID " + reserva.getIdReserva() + ": " + e.getMessage());
                }
            }

            // Depois deleta o cliente
            clienteService.deletarCliente(cliente.getIdCliente());
            System.out.println("Cliente ID " + cliente.getIdCliente() + " deletado com sucesso");

            // Atualizar as tabelas
            carregarReservas();
            carregarClientes();
            atualizarEstatisticas();

            mostrarAlerta("Sucesso", "Cliente e suas reservas foram deletados com sucesso.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao deletar cliente: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}