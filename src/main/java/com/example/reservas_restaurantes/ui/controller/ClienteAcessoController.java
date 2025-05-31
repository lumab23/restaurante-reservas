package com.example.reservas_restaurantes.ui.controller;

import com.example.reservas_restaurantes.enums.StatusReserva;
import com.example.reservas_restaurantes.model.Cliente;
import com.example.reservas_restaurantes.model.Reserva;
import com.example.reservas_restaurantes.service.ClienteService;
import com.example.reservas_restaurantes.service.ReservaService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DateCell;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javafx.stage.Stage;
import com.example.reservas_restaurantes.utils.WindowUtils;

// ... rest of the file ...

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

    @Autowired
    private ClienteService clienteService;
    @Autowired
    private ReservaService reservaService;

    private MainController mainController;

    @FXML
    private void initialize() {
        setupTable();
        setupTelefoneValidation();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void setupTable() {
        colIdReserva.setCellValueFactory(new PropertyValueFactory<>("idReserva"));
        colDataHora.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> 
                cellData.getValue().getDataHora().toString()));
        colNumPessoas.setCellValueFactory(new PropertyValueFactory<>("numPessoas"));
        colStatus.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> 
                cellData.getValue().getStatusReserva().toString()));

        // Configurar coluna de ações
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnCancelar = new Button("Cancelar");
            private final Button btnAlterar = new Button("Alterar Data");
            private final HBox buttons = new HBox(5, btnCancelar, btnAlterar);

            {
                btnCancelar.getStyleClass().add("button-danger");
                btnAlterar.getStyleClass().add("button-secondary");
                
                btnCancelar.setOnAction(event -> {
                    Reserva reserva = getTableView().getItems().get(getIndex());
                    cancelarReserva(reserva);
                });
                
                btnAlterar.setOnAction(event -> {
                    Reserva reserva = getTableView().getItems().get(getIndex());
                    alterarDataReserva(reserva);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Reserva reserva = getTableView().getItems().get(getIndex());
                    boolean podeAlterar = podeAlterarReserva(reserva);
                    btnCancelar.setDisable(!podeAlterar);
                    btnAlterar.setDisable(!podeAlterar);
                    setGraphic(buttons);
                }
            }
        });
    }

    private boolean podeAlterarReserva(Reserva reserva) {
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
            if (newValue == null) return;
            
            // Remove todos os caracteres não numéricos
            String numeros = newValue.replaceAll("[^0-9]", "");
            
            // Se o usuário está apagando, permite a ação
            if (newValue.length() < oldValue.length()) {
                return;
            }
            
            // Formata o número no padrão (XX) XXXXX-XXXX
            if (numeros.length() <= 11) {
                StringBuilder formatado = new StringBuilder();
                if (numeros.length() > 0) {
                    formatado.append("(");
                    formatado.append(numeros.substring(0, Math.min(2, numeros.length())));
                    if (numeros.length() > 2) {
                        formatado.append(") ");
                        formatado.append(numeros.substring(2, Math.min(7, numeros.length())));
                        if (numeros.length() > 7) {
                            formatado.append("-");
                            formatado.append(numeros.substring(7, Math.min(11, numeros.length())));
                        }
                    }
                }
                
                // Só atualiza se o valor for diferente para evitar loop infinito
                String textoFormatado = formatado.toString();
                if (!textoFormatado.equals(newValue)) {
                    txtTelefone.setText(textoFormatado);
                    // Move o cursor para o final
                    txtTelefone.positionCaret(textoFormatado.length());
                }
            }
        });
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
            List<Reserva> reservas = reservaService.listarReservasPorCliente(cliente.getIdCliente());
            
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

    private void alterarDataReserva(Reserva reserva) {
        if (!podeAlterarReserva(reserva)) {
            mostrarMensagem("Não é possível alterar esta reserva. O prazo de 48 horas já passou.", true);
            return;
        }

        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Alterar Data da Reserva");
        dialog.setHeaderText("Selecione a nova data e hora para sua reserva");

        // Configurar botões
        ButtonType confirmarButtonType = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmarButtonType, ButtonType.CANCEL);

        // Criar campos de data e hora
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker datePicker = new DatePicker();
        datePicker.setValue(reserva.getDataHora().toLocalDate());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now().plusDays(2))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Criar ComboBox para horários disponíveis
        ComboBox<LocalTime> horarioComboBox = new ComboBox<>();
        
        // Adicionar horários disponíveis (18:00 até 23:00, em intervalos de 30 minutos)
        LocalTime horarioAtual = ReservaService.HORA_ABERTURA_RESTAURANTE;
        while (!horarioAtual.plusHours(ReservaService.DURACAO_PADRAO_RESERVA_HORAS).isAfter(ReservaService.HORA_FECHAMENTO_RESTAURANTE)) {
            horarioComboBox.getItems().add(horarioAtual);
            horarioAtual = horarioAtual.plusMinutes(ReservaService.INTERVALO_SLOTS_MINUTOS);
        }
        
        // Selecionar horário atual da reserva
        horarioComboBox.setValue(reserva.getDataHora().toLocalTime());

        grid.add(new Label("Data:"), 0, 0);
        grid.add(datePicker, 1, 0);
        grid.add(new Label("Horário:"), 0, 1);
        grid.add(horarioComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Converter resultado para LocalDateTime
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmarButtonType) {
                LocalDate data = datePicker.getValue();
                LocalTime horario = horarioComboBox.getValue();
                
                if (data != null && horario != null) {
                    return LocalDateTime.of(data, horario);
                }
            }
            return null;
        });

        // Mostrar diálogo e processar resultado
        Optional<LocalDateTime> result = dialog.showAndWait();
        result.ifPresent(novaDataHora -> {
            try {
                // Verificar se a nova data está disponível
                if (reservaService.verificarDisponibilidadeMesa(reserva.getIdMesa(), novaDataHora)) {
                    reservaService.alterarDataReserva(reserva.getIdReserva(), novaDataHora);
                    mostrarMensagem("Data da reserva alterada com sucesso!", false);
                    onBuscarReservas(); // Atualiza a lista
                } else {
                    mostrarMensagem("Horário não disponível. Por favor, escolha outro horário.", true);
                }
            } catch (Exception e) {
                mostrarMensagem("Erro ao alterar data da reserva: " + e.getMessage(), true);
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