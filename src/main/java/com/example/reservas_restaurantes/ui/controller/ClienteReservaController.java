package com.example.reservas_restaurantes.ui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.enums.TipoOcasiao;
import com.example.reservas_restaurantes.model.Cliente;
import com.example.reservas_restaurantes.model.Mesa;
import com.example.reservas_restaurantes.model.Reserva;
import com.example.reservas_restaurantes.service.ClienteService;
import com.example.reservas_restaurantes.service.MesaService;
import com.example.reservas_restaurantes.service.ReservaService;
import com.example.reservas_restaurantes.utils.WindowUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClienteReservaController {
    
    // Campos do Cliente
    @FXML private TextField txtNome;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEmail;
    @FXML private DatePicker dpDataNascimento;
    
    // Campos da Reserva
    @FXML private DatePicker dpDataReserva;
    @FXML private ComboBox<String> cbHorario;
    @FXML private Spinner<Integer> spnNumPessoas;
    @FXML private ComboBox<TipoOcasiao> cbOcasiao;
    @FXML private TextArea txtObservacao;
    @FXML private ComboBox<Mesa> cbMesa;
    
    @FXML private Button btnConfirmarReserva;
    @FXML private Button btnVoltar;
    @FXML private VBox containerReserva;
    
    @Autowired
    private ClienteService clienteService;
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private MesaService mesaService;
    
    private MainController mainController;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            setupUI();
            setupValidation();
        });
    }
    
    private void setupUI() {
        // Configurar DatePickers com estilo seguro
        dpDataNascimento.setPromptText("DD/MM/AAAA");
        dpDataNascimento.setStyle("-fx-font-size: 14px;");
        dpDataNascimento.setShowWeekNumbers(false);
        dpDataNascimento.setEditable(false);
        dpDataNascimento.setFocusTraversable(false);
        
        dpDataReserva.setPromptText("DD/MM/AAAA");
        dpDataReserva.setStyle("-fx-font-size: 14px;");
        dpDataReserva.setShowWeekNumbers(false);
        dpDataReserva.setEditable(false);
        dpDataReserva.setFocusTraversable(false);
        
        // Configurar horários disponíveis
        cbHorario.setItems(FXCollections.observableArrayList(
            "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00"
        ));
        
        // Configurar número de pessoas
        spnNumPessoas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2));
        
        // Configurar ocasiões
        cbOcasiao.setItems(FXCollections.observableArrayList(TipoOcasiao.values()));
        cbOcasiao.setValue(TipoOcasiao.JANTAR_A_DOIS);
        
        // Configurar como as ocasiões são exibidas
        cbOcasiao.setCellFactory(param -> new ListCell<TipoOcasiao>() {
            @Override
            protected void updateItem(TipoOcasiao ocasiao, boolean empty) {
                super.updateItem(ocasiao, empty);
                if (empty || ocasiao == null) {
                    setText(null);
                } else {
                    setText(formatarOcasiao(ocasiao.name()));
                }
            }
        });
        
        cbOcasiao.setButtonCell(new ListCell<TipoOcasiao>() {
            @Override
            protected void updateItem(TipoOcasiao ocasiao, boolean empty) {
                super.updateItem(ocasiao, empty);
                if (empty || ocasiao == null) {
                    setText(null);
                } else {
                    setText(formatarOcasiao(ocasiao.name()));
                }
            }
        });
        
        // Carregar todas as mesas inicialmente
        try {
            List<Mesa> todasMesas = mesaService.listarTodasMesas();
            cbMesa.setItems(FXCollections.observableArrayList(todasMesas));
            
            // Configurar como as mesas são exibidas
            cbMesa.setCellFactory(param -> new ListCell<Mesa>() {
                @Override
                protected void updateItem(Mesa mesa, boolean empty) {
                    super.updateItem(mesa, empty);
                    if (empty || mesa == null) {
                        setText(null);
                    } else {
                        setText(String.format("Mesa %d - %s (Cap: %d)", 
                            mesa.getIdMesa(), mesa.getLocalizacao(), mesa.getCapacidade()));
                    }
                }
            });
            
            cbMesa.setButtonCell(new ListCell<Mesa>() {
                @Override
                protected void updateItem(Mesa mesa, boolean empty) {
                    super.updateItem(mesa, empty);
                    if (empty || mesa == null) {
                        setText(null);
                    } else {
                        setText(String.format("Mesa %d - %s (Cap: %d)", 
                            mesa.getIdMesa(), mesa.getLocalizacao(), mesa.getCapacidade()));
                    }
                }
            });
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao carregar mesas: " + e.getMessage(), Alert.AlertType.ERROR);
        }
        
        // Adicionar listeners para atualizar as mesas disponíveis quando a data ou horário mudarem
        dpDataReserva.valueProperty().addListener((obs, oldVal, newVal) -> atualizarMesasDisponiveis());
        cbHorario.valueProperty().addListener((obs, oldVal, newVal) -> atualizarMesasDisponiveis());
        spnNumPessoas.valueProperty().addListener((obs, oldVal, newVal) -> atualizarMesasDisponiveis());
        
        btnConfirmarReserva.setOnAction(e -> confirmarReserva());
        btnVoltar.setOnAction(e -> voltarInicio());
    }
    
    private void setupValidation() {
        // Máscara de telefone
        txtTelefone.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                txtTelefone.setText("");
                return;
            }
            
            // Se o usuário está apagando, permitir a operação
            if (newVal.length() < oldVal.length()) {
                return;
            }

            // Verificar se contém letras
            if (newVal.matches(".*[a-zA-Z].*")) {
                mostrarAlerta("Validação", "Somente números são permitidos no telefone", Alert.AlertType.WARNING);
                txtTelefone.setText(oldVal);
                return;
            }
            
            // Remove todos os caracteres não numéricos
            String digits = newVal.replaceAll("\\D", "");
            
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
                if (!formattedText.equals(newVal)) {
                    txtTelefone.setText(formattedText);
                    // Posiciona o cursor no final
                    txtTelefone.positionCaret(formattedText.length());
                }
            }
        });

        // Validação de email
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
    
    private void atualizarMesasDisponiveis() {
        try {
            // Só filtrar se data, horário e número de pessoas estiverem preenchidos
            if (dpDataReserva.getValue() == null || cbHorario.getValue() == null || spnNumPessoas.getValue() == null) {
                // Carregar todas as mesas se não houver filtro suficiente
                List<Mesa> todasMesas = mesaService.listarTodasMesas();
                cbMesa.setItems(FXCollections.observableArrayList(todasMesas));
                return;
            }

            LocalDateTime dataHoraReserva = LocalDateTime.of(
                dpDataReserva.getValue(),
                LocalTime.parse(cbHorario.getValue())
            );

            // Buscar todas as mesas
            List<Mesa> todasMesas = mesaService.listarTodasMesas();

            // Filtrar mesas disponíveis para o horário selecionado
            List<Mesa> mesasDisponiveis = todasMesas.stream()
                .filter(mesa -> {
                    try {
                        // Verificar se a mesa tem capacidade suficiente
                        if (mesa.getCapacidade() < spnNumPessoas.getValue()) {
                            return false;
                        }
                        // Verificar disponibilidade no horário selecionado
                        return reservaService.verificarDisponibilidadeMesa(mesa.getIdMesa(), dataHoraReserva);
                    } catch (Exception e) {
                        System.err.println("Erro ao verificar disponibilidade da mesa " + mesa.getIdMesa() + ": " + e.getMessage());
                        return false;
                    }
                })
                .collect(Collectors.toList());

            Mesa mesaSelecionada = cbMesa.getValue();
            cbMesa.setItems(FXCollections.observableArrayList(mesasDisponiveis));
            if (mesaSelecionada != null && mesasDisponiveis.contains(mesaSelecionada)) {
                cbMesa.setValue(mesaSelecionada);
            }

            // Só mostrar alerta se todos os filtros estiverem preenchidos e não houver mesas disponíveis
            if (!mesasDisponiveis.isEmpty()) {
                return;
            }
            if (dpDataReserva.getValue() != null && cbHorario.getValue() != null && spnNumPessoas.getValue() != null) {
                mostrarAlerta("Aviso", 
                    "Não há mesas disponíveis para " + spnNumPessoas.getValue() + " pessoas no horário selecionado.\n" +
                    "Por favor, tente outro horário ou data.",
                    Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao atualizar mesas disponíveis: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void confirmarReserva() {
        if (!validarCampos()) {
            return;
        }
        
        try {
            Cliente cliente;
            
            // Tentar buscar cliente existente
            Optional<Cliente> clienteExistente = clienteService.buscarClientePorEmail(txtEmail.getText().trim());
            if (clienteExistente.isPresent()) {
                // Se o cliente existe, usar o cliente existente
                cliente = clienteExistente.get();
                
                // Atualizar os dados do cliente se necessário
                boolean dadosAtualizados = false;
                if (!cliente.getNome().equals(txtNome.getText().trim())) {
                    cliente.setNome(txtNome.getText().trim());
                    dadosAtualizados = true;
                }
                if (!cliente.getTelefone().equals(txtTelefone.getText().trim())) {
                    cliente.setTelefone(txtTelefone.getText().trim());
                    dadosAtualizados = true;
                }
                if (cliente.getDataNascimento() == null || !cliente.getDataNascimento().equals(dpDataNascimento.getValue())) {
                    cliente.setDataNascimento(dpDataNascimento.getValue());
                    dadosAtualizados = true;
                }
                
                // Se algum dado foi atualizado, salvar as alterações
                if (dadosAtualizados) {
                    clienteService.atualizarCliente(cliente);
                }
            } else {
                // Se o cliente não existe, criar um novo
                cliente = new Cliente(
                    txtNome.getText().trim(),
                    txtTelefone.getText().trim(),
                    txtEmail.getText().trim(),
                    dpDataNascimento.getValue()
                );
                clienteService.cadastrarCliente(cliente);
            }
            
            // Criar reserva
            LocalDateTime dataHoraReserva = LocalDateTime.of(
                dpDataReserva.getValue(),
                LocalTime.parse(cbHorario.getValue())
            );
            
            // Fazer a reserva e obter o objeto Reserva completo
            Reserva reserva = reservaService.fazerNovaReserva(
                cliente.getIdCliente(),
                cbMesa.getValue().getIdMesa(),
                dataHoraReserva,
                spnNumPessoas.getValue(),
                cbOcasiao.getValue(),
                txtObservacao.getText().trim()
            );
            
            mostrarAlerta("Sucesso", 
                "Reserva confirmada com sucesso!\n" +
                "ID da Reserva: " + reserva.getIdReserva() + "\n" +
                "Data: " + reserva.getDataHora().toLocalDate().format(DATE_FORMATTER) + "\n" +
                "Horário: " + reserva.getDataHora().toLocalTime().format(TIME_FORMATTER) + "\n" +
                "Mesa: " + reserva.getIdMesa(),
                Alert.AlertType.INFORMATION);
            voltarInicio();
            
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao confirmar reserva: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private boolean validarCampos() {
        if (txtNome.getText().trim().isEmpty()) {
            mostrarAlerta("Validação", "Por favor, informe seu nome.", Alert.AlertType.WARNING);
            return false;
        }
        
        if (txtTelefone.getText().trim().isEmpty()) {
            mostrarAlerta("Validação", "Por favor, informe seu telefone.", Alert.AlertType.WARNING);
            return false;
        }

        // Validar formato do telefone
        if (!txtTelefone.getText().matches("^\\(\\d{2}\\) \\d{5}-\\d{4}$")) {
            mostrarAlerta("Validação", "Telefone deve estar no formato (XX) XXXXX-XXXX", Alert.AlertType.WARNING);
            return false;
        }
        
        if (txtEmail.getText().trim().isEmpty()) {
            mostrarAlerta("Validação", "Por favor, informe seu email.", Alert.AlertType.WARNING);
            return false;
        }

        // Validar formato do email
        if (!txtEmail.getText().contains("@") || (!txtEmail.getText().endsWith(".com") && !txtEmail.getText().endsWith(".br"))) {
            txtEmail.setStyle("-fx-border-color: red;");
            return false;
        }
        
        if (dpDataNascimento.getValue() == null) {
            mostrarAlerta("Validação", "Por favor, informe sua data de nascimento.", Alert.AlertType.WARNING);
            return false;
        }
        
        if (dpDataReserva.getValue() == null) {
            mostrarAlerta("Validação", "Por favor, selecione a data da reserva.", Alert.AlertType.WARNING);
            return false;
        }
        
        if (dpDataReserva.getValue().isBefore(LocalDate.now())) {
            mostrarAlerta("Validação", "A data da reserva não pode ser no passado.", Alert.AlertType.WARNING);
            return false;
        }
        
        if (cbHorario.getValue() == null) {
            mostrarAlerta("Validação", "Por favor, selecione o horário da reserva.", Alert.AlertType.WARNING);
            return false;
        }
        
        if (cbMesa.getValue() == null) {
            mostrarAlerta("Validação", "Por favor, selecione uma mesa.", Alert.AlertType.WARNING);
            return false;
        }
        
        return true;
    }
    
    @FXML
    private void voltarInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
            loader.setControllerFactory(mainController.getSpringContext()::getBean);
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) txtNome.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Sistema de Reservas");
            WindowUtils.configureWindowSize(stage);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta("Erro", "Erro ao voltar: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        
        if (tipo == Alert.AlertType.CONFIRMATION) {
            ButtonType buttonTypeSim = new ButtonType("Sim", ButtonBar.ButtonData.YES);
            ButtonType buttonTypeNao = new ButtonType("Não", ButtonBar.ButtonData.NO);
            alert.getButtonTypes().setAll(buttonTypeSim, buttonTypeNao);
        }
        
        alert.showAndWait();
    }
    
    private String formatarOcasiao(String ocasiao) {
        String[] palavras = ocasiao.replace("_", " ").toLowerCase().split(" ");
        StringBuilder resultado = new StringBuilder();
        
        for (String palavra : palavras) {
            if (!palavra.isEmpty()) {
                resultado.append(Character.toUpperCase(palavra.charAt(0)))
                        .append(palavra.substring(1))
                        .append(" ");
            }
        }
        
        return resultado.toString().trim();
    }
}