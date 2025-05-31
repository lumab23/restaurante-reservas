package com.example.reservas_restaurantes.controller;

import com.example.reservas_restaurantes.model.Pagamento;
import com.example.reservas_restaurantes.model.PagamentoCartao;
import com.example.reservas_restaurantes.service.PagamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/processar")
    public ResponseEntity<?> processarPagamento(
            @RequestParam int idReserva,
            @RequestParam BigDecimal valor,
            @RequestBody(required = false) PagamentoCartao detalhesCartao) {
        try {
            Pagamento pagamento = pagamentoService.processarPagamento(
                    idReserva,
                    valor,
                    detalhesCartao
            );
            return ResponseEntity.ok(pagamento);
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao processar pagamento: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPagamento(@PathVariable int id) {
        try {
            Optional<Pagamento> pagamento = pagamentoService.buscarPagamentoPorId(id);
            if (pagamento.isPresent()) {
                return ResponseEntity.ok(pagamento.get());
            }
            return ResponseEntity.notFound().build();
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body("Erro ao buscar pagamento: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/cartao")
    public ResponseEntity<?> buscarDetalhesCartao(@PathVariable int id) {
        Optional<PagamentoCartao> detalhes = pagamentoService.buscarDetalhesCartaoPorIdPagamento(id);
        if (detalhes.isPresent()) {
            return ResponseEntity.ok(detalhes.get());
        }
        return ResponseEntity.notFound().build();
    }
}