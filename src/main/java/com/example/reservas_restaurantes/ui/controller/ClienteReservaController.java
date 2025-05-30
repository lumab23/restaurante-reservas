package com.example.reservas_restaurantes.ui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.enums.StatusReserva;
import com.example.reservas_restaurantes.enums.TipoOcasiao;
import com.example.reservas_restaurantes.model.Cliente;
import com.example.reservas_restaurantes.model.Mesa;
import com.example.reservas_restaurantes.model.Reserva;
import com.example.reservas_restaurantes.service.ClienteService;
import com.example.reservas_restaurantes.service.MesaService;
import com.example.reservas_restaurantes.service.ReservaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    @FXML
    private void initialize() {
        setupUI();
        setupValidation();
    }
    
    private void setupUI() {
        // Configurar horários disponíveis
        cbHorario.setItems(FXCollections.observableArrayList(
            "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:30", "22:00"
        ));
        
        // Configurar número de pessoas
        spnNumPessoas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2));
        
        // Configurar ocasiões
        cbOcasiao.setItems(FXCollections.observableArrayList(TipoOcasiao.values()));
        cbOcasiao.setValue(TipoOcasiao.JANTAR_A_DOIS);
        
        // Carregar mesas disponíveis
        carregarMesas();
        
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
            
            // Remove todos os caracteres não numéricos
            String digits = newVal.replaceAll("\\D", "");
            
            // Limita a 11 dígitos (DDD + número)
            if (digits.length() > 11) {
                digits = digits.substring(0, 11);
            }
            
            // Formata o número
            StringBuilder formatted = new StringBuilder();
            if (!digits.isEmpty()) {
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
            }
            
            // Atualiza o texto apenas se for diferente para evitar loop infinito
            String formattedText = formatted.toString();
            if (!formattedText.equals(newVal)) {
                txtTelefone.setText(formattedText);
                // Posiciona o cursor no final
                txtTelefone.positionCaret(formattedText.length());
            }
        });
        
        // Atualizar mesas quando mudar data/horário
        dpDataReserva.valueProperty().addListener((obs, oldVal, newVal) -> carregarMesas());
        cbHorario.valueProperty().addListener((obs, oldVal, newVal) -> carregarMesas());
    }
    
    private void carregarMesas() {
        try {
            List<Mesa> mesas = mesaService.listarTodasMesas();
            cbMesa.setItems(FXCollections.observableArrayList(mesas));
            
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
    }
    
    @FXML
    private void confirmarReserva() {
        if (!validarCampos()) {
            return;
        }
        
        try {
            // Criar cliente
            Cliente cliente = new Cliente(
                txtNome.getText().trim(),
                txtTelefone.getText().trim(),
                txtEmail.getText().trim(),
                dpDataNascimento.getValue()
            );
            
            clienteService.cadastrarCliente(cliente);
            
            // Criar reserva
            LocalDateTime dataHoraReserva = LocalDateTime.of(
                dpDataReserva.getValue(),
                LocalTime.parse(cbHorario.getValue())
            );
            
            Reserva reserva = new Reserva(
                cliente.getIdCliente(),
                cbMesa.getValue().getIdMesa(),
                dataHoraReserva,
                spnNumPessoas.getValue(),
                cbOcasiao.getValue(),
                StatusReserva.CONFIRMADA,
                txtObservacao.getText().trim()
            );
            
            reservaService.fazerNovaReserva(
                reserva.getIdCliente(),
                reserva.getIdMesa(),
                reserva.getDataHora(),
                reserva.getNumPessoas(),
                reserva.getOcasiao(),
                reserva.getObservacao()
            );
            
            mostrarAlerta("Sucesso", 
                "Reserva confirmada com sucesso!\n" +
                "Data: " + dpDataReserva.getValue().toString() + "\n" +
                "Horário: " + cbHorario.getValue() + "\n" +
                "Mesa: " + cbMesa.getValue().getIdMesa(),
                Alert.AlertType.INFORMATION);
            
            // Opcional: Abrir tela de pagamento
            abrirTelaPagamento(reserva);
            
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
        
        if (txtEmail.getText().trim().isEmpty()) {
            mostrarAlerta("Validação", "Por favor, informe seu email.", Alert.AlertType.WARNING);
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
    
    private void abrirTelaPagamento(Reserva reserva) {
        // Implementar abertura da tela de pagamento
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Pagamento");
        alert.setHeaderText("Deseja realizar o pagamento agora?");
        alert.setContentText("Você pode pagar antecipadamente ou no dia da reserva.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Implementar tela de pagamento
                mostrarAlerta("Info", "Tela de pagamento será implementada em breve.", Alert.AlertType.INFORMATION);
            }
        });
    }
    
    @FXML
    private void voltarInicio() {
        if (mainController != null) {
            mainController.voltarInicio();
        } else {
            mostrarAlerta("Erro", "Não foi possível voltar ao início: Controlador principal não encontrado.", Alert.AlertType.ERROR);
        }
    }
    
    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}