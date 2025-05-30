package com.example.reservas_restaurantes.ui.controller;

import com.example.reservas_restaurantes.controller.PagamentoController;
import com.example.reservas_restaurantes.enums.MetodoPagamento;
import com.example.reservas_restaurantes.model.PagamentoCartao;
import com.example.reservas_restaurantes.model.PagamentoPix;
import com.example.reservas_restaurantes.model.Reserva;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.springframework.stereotype.Component;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
public class PagamentoUIController implements Initializable {

    @FXML private Text resumoReservaText;
    @FXML private Text valorTotalText;
    @FXML private RadioButton cartaoRadio;
    @FXML private RadioButton pixRadio;
    @FXML private VBox cartaoBox;
    @FXML private VBox pixBox;
    @FXML private TextField numeroCartaoField;
    @FXML private TextField titularField;
    @FXML private TextField validadeField;
    @FXML private TextField cvvField;
    @FXML private ImageView qrCodeImage;
    @FXML private TextField chavePixField;
    @FXML private ToggleGroup metodoPagamento;

    private final PagamentoController pagamentoService;
    private Reserva reserva;
    private MainController mainController;

    // Valores fixos por quantidade de pessoas
    private static final BigDecimal VALOR_ATE_5_PESSOAS = new BigDecimal("50.00");
    private static final BigDecimal VALOR_MAIS_5_PESSOAS = new BigDecimal("100.00");

    public PagamentoUIController(PagamentoController pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupListeners();
        setupValidators();
    }

    private void setupListeners() {
        cartaoRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            cartaoBox.setVisible(newVal);
            pixBox.setVisible(!newVal);
        });

        pixRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            pixBox.setVisible(newVal);
            cartaoBox.setVisible(!newVal);
        });

        // Formatar número do cartão (XXXX XXXX XXXX XXXX)
        numeroCartaoField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                numeroCartaoField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 16) {
                numeroCartaoField.setText(oldVal);
            }
        });

        // Formatar validade (MM/AA)
        validadeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                validadeField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 4) {
                validadeField.setText(oldVal);
            }
            if (newVal.length() == 2 && !newVal.contains("/")) {
                validadeField.setText(newVal + "/");
            }
        });

        // Formatar CVV (apenas números, max 3 dígitos)
        cvvField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                cvvField.setText(newVal.replaceAll("[^\\d]", ""));
            }
            if (newVal.length() > 3) {
                cvvField.setText(oldVal);
            }
        });
    }

    private void setupValidators() {
        // Validação do número do cartão (Luhn algorithm)
        numeroCartaoField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                String numero = numeroCartaoField.getText().replaceAll("\\s", "");
                if (!isValidCardNumber(numero)) {
                    mostrarAlerta("Erro", "Número do cartão inválido", Alert.AlertType.ERROR);
                    numeroCartaoField.setText("");
                }
            }
        });

        // Validação da data de validade
        validadeField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                String validade = validadeField.getText();
                if (!isValidExpiryDate(validade)) {
                    mostrarAlerta("Erro", "Data de validade inválida", Alert.AlertType.ERROR);
                    validadeField.setText("");
                }
            }
        });
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
        atualizarResumoReserva();
        gerarChavePix();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void atualizarResumoReserva() {
        if (reserva != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            resumoReservaText.setText(String.format("Reserva para %d pessoas em %s",
                    reserva.getNumPessoas(),
                    reserva.getDataHora().format(formatter)));
            
            BigDecimal valorTotal = calcularValorTotal();
            valorTotalText.setText(String.format("Valor Total: R$ %.2f", valorTotal));
        }
    }

    private BigDecimal calcularValorTotal() {
        if (reserva == null) {
            return BigDecimal.ZERO;
        }

        return reserva.getNumPessoas() <= 5 ? VALOR_ATE_5_PESSOAS : VALOR_MAIS_5_PESSOAS;
    }

    private void gerarChavePix() {
        // TODO: Implementar geração real de chave PIX
        String chavePix = "restaurante-" + reserva.getIdReserva() + "@pix.com";
        chavePixField.setText(chavePix);
        // TODO: Gerar e exibir QR Code
    }

    @FXML
    private void copiarChavePix() {
        String chave = chavePixField.getText();
        if (chave != null && !chave.isEmpty()) {
            StringSelection selection = new StringSelection(chave);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            mostrarAlerta("Sucesso", "Chave PIX copiada para a área de transferência", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void confirmarPagamento() {
        try {
            if (cartaoRadio.isSelected()) {
                processarPagamentoCartao();
            } else {
                processarPagamentoPix();
            }
        } catch (Exception e) {
            mostrarAlerta("Erro", "Erro ao processar pagamento: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void processarPagamentoCartao() {
        if (!validarCamposCartao()) {
            return;
        }

        PagamentoCartao detalhesCartao = new PagamentoCartao();
        detalhesCartao.setNumeroCartao(numeroCartaoField.getText());
        detalhesCartao.setTitular(titularField.getText());
        detalhesCartao.setValidade(parseExpiryDate(validadeField.getText()));
        detalhesCartao.setCvv(cvvField.getText());

        var pagamento = pagamentoService.processarPagamento(
                reserva.getIdReserva(),
                calcularValorTotal(),
                MetodoPagamento.CARTAO,
                detalhesCartao,
                null
        );

        if (pagamento != null) {
            mostrarAlerta("Sucesso", "Pagamento realizado com sucesso!", Alert.AlertType.INFORMATION);
            voltarInicio();
        }
    }

    private void processarPagamentoPix() {
        PagamentoPix detalhesPix = new PagamentoPix();
        detalhesPix.setChavePix(chavePixField.getText());

        var pagamento = pagamentoService.processarPagamento(
                reserva.getIdReserva(),
                calcularValorTotal(),
                MetodoPagamento.PIX,
                null,
                detalhesPix
        );

        if (pagamento != null) {
            mostrarAlerta("Sucesso", "Pagamento PIX registrado! Aguarde a confirmação.", Alert.AlertType.INFORMATION);
            voltarInicio();
        }
    }

    private boolean validarCamposCartao() {
        if (numeroCartaoField.getText().isEmpty() ||
            titularField.getText().isEmpty() ||
            validadeField.getText().isEmpty() ||
            cvvField.getText().isEmpty()) {
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
} 