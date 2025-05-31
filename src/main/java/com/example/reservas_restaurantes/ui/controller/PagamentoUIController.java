package com.example.reservas_restaurantes.ui.controller;

import com.example.reservas_restaurantes.controller.PagamentoController;
import com.example.reservas_restaurantes.controller.MesaController;
import com.example.reservas_restaurantes.model.*;
import com.example.reservas_restaurantes.service.ClienteService;
import com.example.reservas_restaurantes.service.ReservaService;
import com.example.reservas_restaurantes.utils.WindowUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.io.IOException;

@Component
public class PagamentoUIController implements Initializable {

    @FXML private Text clienteText;
    @FXML private Text mesaText;
    @FXML private Text dataHoraText;
    @FXML private Text numPessoasText;
    @FXML private Text ocasiaoText;
    @FXML private Text statusText;
    @FXML private Text valorTotalText;
    @FXML private VBox cartaoBox;
    @FXML private TextField numeroCartaoField;
    @FXML private TextField titularField;
    @FXML private TextField validadeField;
    @FXML private TextField cvvField;
    @FXML private Stage stage;

    @Autowired
    private PagamentoController pagamentoService;
    
    @Autowired
    private ReservaService reservaService;
    
    @Autowired
    private ClienteService clienteService;
    
    @Autowired
    private MesaController mesaController;
    
    private Reserva reserva;
    private MainController mainController;

    private static final BigDecimal VALOR_ATE_5_PESSOAS = new BigDecimal("50.00");
    private static final BigDecimal VALOR_MAIS_5_PESSOAS = new BigDecimal("100.00");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Inicializando PagamentoUIController...");
        setupValidators();
        
        if (valorTotalText != null) {
            valorTotalText.setText("Valor Total: R$ 0,00");
        }
    }

    private void setupValidators() {
        // Formatar número do cartão (XXXX XXXX XXXX XXXX)
        if (numeroCartaoField != null) {
            numeroCartaoField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null) {
                    numeroCartaoField.setText("");
                    return;
                }
                
                // Se o usuário está apagando, permitir a operação
                if (newVal.length() < oldVal.length()) {
                    return;
                }
                
                // Remove todos os caracteres não numéricos
                String digits = newVal.replaceAll("[^\\d]", "");
                
                // Limita a 16 dígitos
                if (digits.length() > 16) {
                    digits = digits.substring(0, 16);
                }
                
                // Formata o número apenas se houver dígitos
                if (!digits.isEmpty()) {
                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < digits.length(); i++) {
                        if (i > 0 && i % 4 == 0) {
                            formatted.append(" ");
                        }
                        formatted.append(digits.charAt(i));
                    }
                    
                    // Atualiza o texto apenas se for diferente para evitar loop infinito
                    String formattedText = formatted.toString();
                    if (!formattedText.equals(newVal)) {
                        numeroCartaoField.setText(formattedText);
                        // Posiciona o cursor no final
                        numeroCartaoField.positionCaret(formattedText.length());
                    }
                }
            });

            // Adicionar tooltip com números de teste
            numeroCartaoField.setTooltip(new Tooltip(
                "Números de teste válidos:\n" +
                "Visa: 4532 0151 1283 0366\n" +
                "Mastercard: 5424 0000 0000 0015\n" +
                "American Express: 3782 8224 6310 005\n" +
                "Discover: 6011 0000 0000 0012"
            ));
        }

        // Formatar validade (MM/AA)
        if (validadeField != null) {
            validadeField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null) {
                    validadeField.setText("");
                    return;
                }
                
                // Se o usuário está apagando, permitir a operação
                if (newVal.length() < oldVal.length()) {
                    return;
                }
                
                // Remove todos os caracteres não numéricos
                String digits = newVal.replaceAll("[^\\d]", "");
                
                // Limita a 4 dígitos
                if (digits.length() > 4) {
                    digits = digits.substring(0, 4);
                }
                
                // Formata a data apenas se houver dígitos
                if (!digits.isEmpty()) {
                    StringBuilder formatted = new StringBuilder();
                    formatted.append(digits.substring(0, Math.min(2, digits.length())));
                    
                    if (digits.length() > 2) {
                        formatted.append("/");
                        formatted.append(digits.substring(2));
                    }
                    
                    // Atualiza o texto apenas se for diferente para evitar loop infinito
                    String formattedText = formatted.toString();
                    if (!formattedText.equals(newVal)) {
                        validadeField.setText(formattedText);
                        // Posiciona o cursor no final
                        validadeField.positionCaret(formattedText.length());
                    }
                }
            });
        }

        // Formatar CVV (apenas números, max 3 dígitos)
        if (cvvField != null) {
            cvvField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal == null) {
                    cvvField.setText("");
                    return;
                }
                
                // Se o usuário está apagando, permitir a operação
                if (newVal.length() < oldVal.length()) {
                    return;
                }
                
                // Remove todos os caracteres não numéricos
                String digits = newVal.replaceAll("[^\\d]", "");
                
                // Limita a 3 dígitos
                if (digits.length() > 3) {
                    digits = digits.substring(0, 3);
                }
                
                // Atualiza o texto apenas se for diferente para evitar loop infinito
                if (!digits.equals(newVal)) {
                    cvvField.setText(digits);
                    // Posiciona o cursor no final
                    cvvField.positionCaret(digits.length());
                }
            });
        }

        // Validação do número do cartão
        if (numeroCartaoField != null) {
            numeroCartaoField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) { // Quando o campo perde o foco
                    String numero = numeroCartaoField.getText().replaceAll("\\s", "");
                    if (!numero.isEmpty()) {
                        if (numero.length() < 13 || numero.length() > 19) {
                            mostrarAlerta("Erro", 
                                "O número do cartão deve ter entre 13 e 19 dígitos.\n" +
                                "Use um dos números de teste disponíveis no tooltip do campo.",
                                Alert.AlertType.ERROR);
                            numeroCartaoField.setText("");
                        } else if (!isValidCardNumber(numero)) {
                            mostrarAlerta("Erro", 
                                "Número do cartão inválido.\n" +
                                "O número não passou na validação do algoritmo de Luhn.\n" +
                                "Use um dos números de teste disponíveis no tooltip do campo.",
                                Alert.AlertType.ERROR);
                            numeroCartaoField.setText("");
                        }
                    }
                }
            });
        }

        // Validação da data de validade
        if (validadeField != null) {
            validadeField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    String validade = validadeField.getText();
                    if (!validade.isEmpty() && !isValidExpiryDate(validade)) {
                        mostrarAlerta("Erro", "Data de validade inválida", Alert.AlertType.ERROR);
                        validadeField.setText("");
                    }
                }
            });
        }
    }

    public void setReserva(Reserva reserva) {
        System.out.println("Definindo reserva no PagamentoUIController: " + 
                          (reserva != null ? reserva.getIdReserva() : "null"));
        this.reserva = reserva;
        atualizarResumoReserva();
    }

    public void setMainController(MainController mainController) {
        System.out.println("Definindo MainController no PagamentoUIController");
        this.mainController = mainController;
    }

    private void atualizarResumoReserva() {
        if (reserva != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            try {
                // Buscar informações do cliente e mesa usando os controllers
                Reserva reservaCompleta = reservaService.buscarReservaPorId(reserva.getIdReserva());
                Cliente cliente = clienteService.buscarClientePorId(reservaCompleta.getIdCliente());
                Mesa mesa = mesaController.buscarMesaPorId(reservaCompleta.getIdMesa());
                
                if (cliente == null || mesa == null) {
                    throw new RuntimeException("Não foi possível encontrar informações do cliente ou da mesa");
                }
                
                clienteText.setText(cliente.getNome());
                mesaText.setText(mesa.getLocalizacao() + " (Capacidade: " + mesa.getCapacidade() + " pessoas)");
                dataHoraText.setText(reservaCompleta.getDataHora().format(formatter));
                numPessoasText.setText(String.valueOf(reservaCompleta.getNumPessoas()));
                ocasiaoText.setText(formatarOcasiao(reservaCompleta.getOcasiao().name()));
                statusText.setText(formatarStatus(reservaCompleta.getStatusReserva().name()));
                
                BigDecimal valorTotal = calcularValorTotal();
                valorTotalText.setText(String.format("R$ %.2f", valorTotal));
            } catch (Exception e) {
                mostrarAlerta("Erro", "Erro ao carregar detalhes da reserva: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private String formatarOcasiao(String ocasiao) {
        return ocasiao.replace("_", " ").toLowerCase();
    }

    private String formatarStatus(String status) {
        switch (status) {
            case "PENDENTE": return "Aguardando Confirmação";
            case "CONFIRMADA": return "Confirmada";
            case "CANCELADA": return "Cancelada";
            default: return status;
        }
    }

    private BigDecimal calcularValorTotal() {
        if (reserva == null) {
            return BigDecimal.ZERO;
        }
        return reserva.getNumPessoas() <= 5 ? VALOR_ATE_5_PESSOAS : VALOR_MAIS_5_PESSOAS;
    }

    @FXML
    private void confirmarPagamento() {
        try {
            System.out.println("Confirmando pagamento...");
            
            if (reserva == null) {
                mostrarAlerta("Erro", "Informações da reserva não encontradas", Alert.AlertType.ERROR);
                return;
            }
            
            if (pagamentoService == null) {
                mostrarAlerta("Erro", "Serviço de pagamento não inicializado", Alert.AlertType.ERROR);
                return;
            }
            
            processarPagamentoCartao();
        } catch (Exception e) {
            System.err.println("Erro ao processar pagamento: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao processar pagamento: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void processarPagamentoCartao() {
        if (!validarCamposCartao()) {
            return;
        }

        try {
            PagamentoCartao detalhesCartao = new PagamentoCartao();
            detalhesCartao.setNumeroCartao(numeroCartaoField.getText().replaceAll("\\s", ""));
            detalhesCartao.setTitular(titularField.getText());
            detalhesCartao.setValidade(parseExpiryDate(validadeField.getText()));
            detalhesCartao.setCvv(cvvField.getText());

            var pagamento = pagamentoService.processarPagamento(
                    reserva.getIdReserva(),
                    calcularValorTotal(),
                    detalhesCartao
            );

            if (pagamento != null) {
                mostrarAlerta("Sucesso", "Pagamento realizado com sucesso!", Alert.AlertType.INFORMATION);
                
                // Primeiro voltar para a tela principal
                if (mainController != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-view.fxml"));
                        loader.setControllerFactory(mainController.getSpringContext()::getBean);
                        Parent root = loader.load();
                        
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
                        
                        // Obter o Stage da janela principal (não da janela de pagamento)
                        Stage mainStage = (Stage) stage.getOwner();
                        if (mainStage == null) {
                            mainStage = new Stage();
                        }
                        
                        mainStage.setScene(scene);
                        mainStage.setTitle("Sistema de Reservas");
                        WindowUtils.configureWindowSize(mainStage);
                        mainStage.show();
                        
                        // Só então fechar a janela de pagamento
                        fecharJanela();
                    } catch (IOException e) {
                        mostrarAlerta("Erro", "Erro ao voltar para a tela principal: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                }
            }
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao processar pagamento: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private boolean validarCamposCartao() {
        if (numeroCartaoField == null || titularField == null || 
            validadeField == null || cvvField == null) {
            mostrarAlerta("Erro", "Campos do cartão não inicializados", Alert.AlertType.ERROR);
            return false;
        }

        if (numeroCartaoField.getText().trim().isEmpty() ||
            titularField.getText().trim().isEmpty() ||
            validadeField.getText().trim().isEmpty() ||
            cvvField.getText().trim().isEmpty()) {
            mostrarAlerta("Erro", "Preencha todos os campos do cartão", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private boolean isValidCardNumber(String number) {
        if (number == null || number.length() < 13 || number.length() > 19) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(number.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    private boolean isValidExpiryDate(String expiry) {
        if (expiry == null || !expiry.matches("\\d{2}/\\d{2}")) {
            return false;
        }

        String[] parts = expiry.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        if (month < 1 || month > 12) {
            return false;
        }

        LocalDate now = LocalDate.now();
        int currentYear = now.getYear() % 100;
        int currentMonth = now.getMonthValue();

        return (year > currentYear) || (year == currentYear && month >= currentMonth);
    }

    private LocalDate parseExpiryDate(String expiry) {
        String[] parts = expiry.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = 2000 + Integer.parseInt(parts[1]); // Assume século 21
        return LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);
    }

    @FXML
    private void cancelarPagamento() {
        voltarInicio();
        fecharJanela();
    }

    private void voltarInicio() {
        if (mainController != null) {
            mainController.voltarInicio();
        }
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public void fecharJanela() {
        if (stage != null) {
            stage.close();
        }
    }
}