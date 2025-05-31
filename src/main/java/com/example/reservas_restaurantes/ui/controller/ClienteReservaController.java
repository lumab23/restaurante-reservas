package com.example.reservas_restaurantes.ui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
import java.net.URL;
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
            
            // Se o usuário está apagando, permitir a operação
            if (newVal.length() < oldVal.length()) {
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
        
        // Atualizar mesas quando mudar data/horário
        dpDataReserva.valueProperty().addListener((obs, oldVal, newVal) -> carregarMesas());
        cbHorario.valueProperty().addListener((obs, oldVal, newVal) -> carregarMesas());
    }
    
    private void carregarMesas() {
        try {
            if (dpDataReserva.getValue() == null || cbHorario.getValue() == null) {
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

            // Atualizar o ComboBox com as mesas disponíveis
            cbMesa.setItems(FXCollections.observableArrayList(mesasDisponiveis));
            
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

            // Se não houver mesas disponíveis, mostrar alerta
            if (mesasDisponiveis.isEmpty()) {
                mostrarAlerta("Aviso", 
                    "Não há mesas disponíveis para " + spnNumPessoas.getValue() + " pessoas no horário selecionado.\n" +
                    "Por favor, tente outro horário ou data.",
                    Alert.AlertType.WARNING);
            }
            
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
            Cliente cliente;
            
            // Tentar buscar cliente existente, mas não lançar exceção se não encontrar
            try {
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
            } catch (Exception e) {
                // Se houver qualquer erro ao buscar o cliente, criar um novo
                System.out.println("Erro ao buscar cliente existente, criando novo cliente: " + e.getMessage());
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
                "Data: " + dpDataReserva.getValue().toString() + "\n" +
                "Horário: " + cbHorario.getValue() + "\n" +
                "Mesa: " + cbMesa.getValue().getIdMesa(),
                Alert.AlertType.INFORMATION);
            
            // Abrir tela de pagamento com a reserva completa
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Pagamento");
        alert.setHeaderText("Deseja realizar o pagamento agora?");
        alert.setContentText("Você pode pagar antecipadamente ou no dia da reserva.");
        
        ButtonType btnPagarAgora = new ButtonType("Pagar Agora");
        ButtonType btnDepois = new ButtonType("Pagar Depois");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(btnPagarAgora, btnDepois, btnCancelar);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == btnPagarAgora) {
                try {
                    // Usar ClassPathResource do Spring para carregar o FXML
                    ClassPathResource resource = new ClassPathResource("fxml/pagamento.fxml");
                    URL resourceUrl;
                    try {
                        resourceUrl = resource.getURL();
                        System.out.println("Tentando carregar FXML de: " + resourceUrl);
                    } catch (IOException e) {
                        throw new IOException("Não foi possível acessar o arquivo pagamento.fxml: " + e.getMessage());
                    }
                    
                    FXMLLoader loader = new FXMLLoader(resourceUrl);
                    // MUDANÇA PRINCIPAL: Usar o Spring Context para criar o controller
                    loader.setControllerFactory(mainController.getSpringContext()::getBean);
                    Parent root = loader.load();
                    
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setTitle("Pagamento da Reserva");
                    stage.setScene(scene);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    
                    PagamentoUIController pagamentoController = loader.getController();
                    pagamentoController.setStage(stage);
                    pagamentoController.setReserva(reserva);
                    pagamentoController.setMainController(mainController);
                    
                    stage.showAndWait();
                } catch (IOException e) {
                    System.err.println("Erro detalhado ao abrir tela de pagamento:");
                    e.printStackTrace();
                    String resourcePath;
                    try {
                        resourcePath = new ClassPathResource("fxml/pagamento.fxml").getURL().toString();
                    } catch (IOException ex) {
                        resourcePath = "não foi possível determinar o caminho";
                    }
                    mostrarAlerta("Erro", 
                        "Erro ao abrir tela de pagamento: " + e.getMessage() + "\n" +
                        "Caminho do arquivo: " + resourcePath,
                        Alert.AlertType.ERROR);
                }
            } else if (response == btnDepois) {
                mostrarAlerta("Informação", 
                    "Você poderá realizar o pagamento no dia da reserva.\n" +
                    "Data: " + reserva.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n" +
                    "Horário: " + reserva.getDataHora().format(DateTimeFormatter.ofPattern("HH:mm")),
                    Alert.AlertType.INFORMATION);
                voltarInicio();
            } else {
                voltarInicio();
            }
        });
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
        alert.showAndWait();
    }
}