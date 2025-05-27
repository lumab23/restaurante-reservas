package com.example.reservas_restaurantes.controller;

import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.enums.MetodoPagamento;
import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.exception.EntidadeNaoEncontradaException;
import com.example.reservas_restaurantes.model.Pagamento;
import com.example.reservas_restaurantes.model.PagamentoCartao;
import com.example.reservas_restaurantes.model.PagamentoPix;
import com.example.reservas_restaurantes.service.PagamentoService;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    public Pagamento processarPagamento(int idReserva, BigDecimal valor, MetodoPagamento metodo,
                                        PagamentoCartao detalhesCartao, PagamentoPix detalhesPix) {
        try {
            Pagamento novoPagamento = pagamentoService.processarPagamento(idReserva, valor, metodo, detalhesCartao, detalhesPix);
            System.out.println("Controller: Pagamento processado com sucesso - ID: " + novoPagamento.getIdPagamento());
            return novoPagamento;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao processar pagamento - " + e.getMessage());
            return null;
        }
    }

    public Pagamento buscarPagamentoPorId(int idPagamento) {
        try {
            Pagamento pagamento = pagamentoService.buscarPagamentoPorId(idPagamento);
            System.out.println("Controller: Pagamento encontrado - ID: " + (pagamento != null ? pagamento.getIdPagamento() : "N/A"));
            return pagamento;
        } catch (EntidadeNaoEncontradaException e) {
            System.err.println("Controller: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Controller: Erro inesperado ao buscar pagamento - " + e.getMessage());
            return null;
        }
    }

    public List<Pagamento> listarPagamentosPorReserva(int idReserva) {
        try {
            List<Pagamento> pagamentos = pagamentoService.listarPagamentosPorReserva(idReserva);
            System.out.println("Controller: " + pagamentos.size() + " pagamentos listados para a reserva ID " + idReserva);
            return pagamentos;
        } catch (Exception e) {
            System.err.println("Controller: Erro ao listar pagamentos - " + e.getMessage());
            return List.of();
        }
    }
}