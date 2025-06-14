package com.example.reservas_restaurantes.ui.controller;

import com.example.reservas_restaurantes.enums.StatusReserva;
import com.example.reservas_restaurantes.model.Cliente;
import com.example.reservas_restaurantes.model.Reserva;
import com.example.reservas_restaurantes.service.ClienteService;
import com.example.reservas_restaurantes.service.ReservaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.stage.Stage;
import com.example.reservas_restaurantes.utils.WindowUtils;
import java.time.format.DateTimeFormatter;

@Component
public class ClienteAcessoController {

    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;
    @FXML private TableView<Reserva> tableReservas;
    @FXML private TableColumn<Reserva, Integer> colIdReserva;
    @FXML private TableColumn<Reserva, String> colDataHora;
    @FXML private TableColumn<Reserva, Integer> colNumPessoas;
    @FXML private TableColumn<Reserva, String> colStatus;
    @FXML private TableColumn<Reserva, Void> colAcoes;
    @FXML private Label lblMensagem;
    @FXML private DatePicker dpFiltroData;

    @Autowired
    private ClienteService clienteService;
    @Autowired
    private ReservaService reservaService;

    private MainController mainController;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        setupTable();
        setupTelefoneValidation();
        setupEmailValidation();
        setupDatePickers();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void setupTable() {
        colIdReserva.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        colDataHora.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> 
                cellData.getValue().getDataHora().format(DATETIME_FORMATTER)));
        colNumPessoas.setCellValueFactory(new PropertyValueFactory<>("numPessoas"));
        colStatus.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> 
                cellData.getValue().getStatusReserva().toString()));

        // Configurar coluna de ações
        setupColunaAcoes();
    }

    private void setupColunaAcoes() {
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnCancelar = new Button("Cancelar");
            private final HBox buttons = new HBox(btnCancelar);

            {
                buttons.setSpacing(5);
                btnCancelar.getStyleClass().add("button-danger");
                btnCancelar.setOnAction(e -> {
                    Reserva reserva = getTableView().getItems().get(getIndex());
                    cancelarReserva(reserva);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reserva reserva = getTableView().getItems().get(getIndex());
                    boolean podeCancelar = podeCancelarReserva(reserva);
                    btnCancelar.setDisable(!podeCancelar);
                    setGraphic(buttons);
                }
            }
        });
    }

    private boolean podeCancelarReserva(Reserva reserva) {
        if (reserva.getStatusReserva() != StatusReserva.CONFIRMADA) {
            return false;
        }
        
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataReserva = reserva.getDataHora();
        long horasAteReserva = ChronoUnit.HOURS.between(agora, dataReserva);
        
        return horasAteReserva >= 48; // 2 dias = 48 horas
    }

    private void setupTelefoneValidation() {
        txtTelefone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                txtTelefone.setText("");
                return;
            }
            
            // Se o usuário está apagando, permite a ação
            if (newValue.length() < oldValue.length()) {
                return;
            }

            // Verificar se contém letras
            if (newValue.matches(".*[a-zA-Z].*")) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validação");
                alert.setHeaderText(null);
                alert.setContentText("Somente números são permitidos no telefone");
                alert.showAndWait();
                txtTelefone.setText(oldValue);
                return;
            }
            
            // Remove todos os caracteres não numéricos
            String digits = newValue.replaceAll("\\D", "");
            
            // Limita a 11 dígitos (DDD + número)
            if (digits.length() > 11) {
                digits = digits.substring(0, 11);
            }
            
            // Formata o número apenas se houver dígitos
            if (!digits.isEmpty()) {
                StringBuilder formatted = new StringBuilder();
                formatted.append("(");
                formatted.append(digits.substring(0, Math.min(2, digits.length())));
                
                if (digits.length() > 2) {
                    formatted.append(") ");
                    formatted.append(digits.substring(2, Math.min(7, digits.length())));
                    
                    if (digits.length() > 7) {
                        formatted.append("-");
                        formatted.append(digits.substring(7));
                    }
                }
                
                // Atualiza o texto apenas se for diferente para evitar loop infinito
                String formattedText = formatted.toString();
                if (!formattedText.equals(newValue)) {
                    txtTelefone.setText(formattedText);
                    // Posiciona o cursor no final
                    txtTelefone.positionCaret(formattedText.length());
                }
            }
        });
    }

    private void setupEmailValidation() {
        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                if (!newVal.contains("@") || (!newVal.endsWith(".com") && !newVal.endsWith(".br"))) {
                    txtEmail.setStyle("-fx-border-color: red;");
                } else {
                    txtEmail.setStyle("");
                }
            } else {
                txtEmail.setStyle("");
            }
        });
    }

    private void setupDatePickers() {
        if (dpFiltroData != null) {
            dpFiltroData.setPromptText("DD/MM/AAAA");
            dpFiltroData.setStyle("-fx-font-size: 14px;");
            dpFiltroData.setShowWeekNumbers(false);
            dpFiltroData.setEditable(false);
            dpFiltroData.setFocusTraversable(false);
        }
    }

    @FXML
    private void onBuscarReservas() {
        String email = txtEmail.getText().trim();
        String telefone = txtTelefone.getText().trim();

        if (email.isEmpty() || telefone.isEmpty()) {
            mostrarMensagem("Por favor, preencha email e telefone.", true);
            return;
        }

        // Validar formato do telefone
        if (!telefone.matches("^\\(\\d{2}\\) \\d{5}-\\d{4}$")) {
            mostrarMensagem("Telefone deve estar no formato (XX) XXXXX-XXXX", true);
            return;
        }

        try {
            Optional<Cliente> clienteOpt = clienteService.buscarClientePorEmail(email);
            if (clienteOpt.isEmpty() || !clienteOpt.get().getTelefone().equals(telefone)) {
                mostrarMensagem("Cliente não encontrado. Verifique email e telefone.", true);
                return;
            }

            Cliente cliente = clienteOpt.get();
            List<Reserva> reservas = reservaService.listarReservasPorCliente(cliente.getId());
            
            if (reservas.isEmpty()) {
                mostrarMensagem("Nenhuma reserva encontrada para este cliente.", false);
                tableReservas.setVisible(false);
            } else {
                tableReservas.setItems(FXCollections.observableArrayList(reservas));
                tableReservas.setVisible(true);
                lblMensagem.setVisible(false);
            }
        } catch (Exception e) {
            mostrarMensagem("Erro ao buscar reservas: " + e.getMessage(), true);
        }
    }

    private void cancelarReserva(Reserva reserva) {
        LocalDateTime agora = LocalDateTime.now();
        long horasAteReserva = ChronoUnit.HOURS.between(agora, reserva.getDataHora());
        
        if (horasAteReserva < 48) {
            mostrarMensagem("Não é possível cancelar esta reserva. O prazo de 48 horas já passou.", true);
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Cancelamento");
        confirmacao.setHeaderText("Cancelar Reserva");
        confirmacao.setContentText("Tem certeza que deseja cancelar esta reserva?");

        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    reservaService.cancelarReserva(reserva.getIdReserva());
                    mostrarMensagem("Reserva cancelada com sucesso!", false);
                    onBuscarReservas(); // Atualiza a lista
                } catch (Exception e) {
                    mostrarMensagem("Erro ao cancelar reserva: " + e.getMessage(), true);
                }
            }
        });
    }

    private void mostrarMensagem(String mensagem, boolean erro) {
        lblMensagem.setText(mensagem);
        lblMensagem.getStyleClass().clear();
        lblMensagem.getStyleClass().add("message-label");
        if (erro) {
            lblMensagem.getStyleClass().add("error");
        }
        lblMensagem.setVisible(true);
    }

    @FXML
    private void onVoltar() {
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
            mostrarMensagem("Erro ao voltar: " + e.getMessage(), true);
        }
    }
} 