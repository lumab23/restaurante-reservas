package com.example.reservas_restaurantes.service; 

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reservas_restaurantes.enums.MetodoPagamento;
import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.exception.EntidadeNaoEncontradaException;
import com.example.reservas_restaurantes.model.Pagamento;
import com.example.reservas_restaurantes.model.PagamentoCartao;
import com.example.reservas_restaurantes.model.PagamentoPix;
import com.example.reservas_restaurantes.model.Reserva;
import com.example.reservas_restaurantes.repository.PagamentoCartaoRepository;
import com.example.reservas_restaurantes.repository.PagamentoPixRepository;
import com.example.reservas_restaurantes.repository.PagamentoRepository;
import com.example.reservas_restaurantes.repository.ReservaRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PagamentoCartaoRepository pagamentoCartaoRepository;
    private final PagamentoPixRepository pagamentoPixRepository;
    private final ReservaRepository reservaRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository,
                            PagamentoCartaoRepository pagamentoCartaoRepository,
                            PagamentoPixRepository pagamentoPixRepository,
                            ReservaRepository reservaRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.pagamentoCartaoRepository = pagamentoCartaoRepository;
        this.pagamentoPixRepository = pagamentoPixRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional
    public Pagamento processarPagamento(int idReserva, BigDecimal valor, MetodoPagamento metodo,
                                        PagamentoCartao detalhesCartao, PagamentoPix detalhesPix)
            throws BusinessRuleException, EntidadeNaoEncontradaException {

        // 1. Validar Reserva
        try {
            Optional<Reserva> reservaOpt = reservaRepository.buscarPorId(idReserva);
            if (reservaOpt.isEmpty()) {
                throw new EntidadeNaoEncontradaException("Reserva com ID " + idReserva + " não encontrada para processar o pagamento.");
            }
        } catch (SQLException e) {
            throw new EntidadeNaoEncontradaException("Erro de banco de dados ao buscar reserva: " + e.getMessage(), e);
        }

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Valor do pagamento deve ser positivo.");
        }
        if (metodo == null) {
            throw new BusinessRuleException("Método de pagamento é obrigatório.");
        }

        // 2. Criar e Salvar Pagamento Principal
        Pagamento novoPagamento = new Pagamento(idReserva, valor, metodo);
        try {
            pagamentoRepository.salvar(novoPagamento); // O DAO deve popular o ID
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao salvar pagamento principal: " + e.getMessage(), e);
        }

        // 3. Salvar Detalhes Específicos do Pagamento
        try {
            if (metodo == MetodoPagamento.CARTAO) {
                if (detalhesCartao == null) {
                    throw new BusinessRuleException("Detalhes do cartão são obrigatórios para pagamento com cartão.");
                }
                detalhesCartao.setIdPagamento(novoPagamento.getIdPagamento());
                pagamentoCartaoRepository.salvar(detalhesCartao);
            } else if (metodo == MetodoPagamento.PIX) {
                if (detalhesPix == null) {
                    throw new BusinessRuleException("Detalhes do PIX são obrigatórios para pagamento com PIX.");
                }
                detalhesPix.setIdPagamento(novoPagamento.getIdPagamento());
                pagamentoPixRepository.salvar(detalhesPix);
            }
        } catch (SQLException e) {
            // A exceção @Transactional fará o rollback de novoPagamento.
            throw new BusinessRuleException("Erro ao salvar detalhes do pagamento: " + e.getMessage(), e);
        }

        return novoPagamento;
    }

    public Pagamento buscarPagamentoPorId(int idPagamento) throws EntidadeNaoEncontradaException {
        try {
            Optional<Pagamento> pagamentoOpt = pagamentoRepository.buscarPorId(idPagamento);
            if (pagamentoOpt.isEmpty()) {
                throw new EntidadeNaoEncontradaException("Pagamento com ID " + idPagamento + " não encontrado.");
            }
            return pagamentoOpt.get();
        } catch (SQLException e) {
            throw new EntidadeNaoEncontradaException("Erro de banco de dados ao buscar pagamento: " + e.getMessage(), e);
        }
    }

    public Optional<PagamentoCartao> buscarDetalhesCartaoPorIdPagamento(int idPagamento) {
        try {
            return pagamentoCartaoRepository.buscarPorIdPagamento(idPagamento);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar detalhes do cartão: " + e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<PagamentoPix> buscarDetalhesPixPorIdPagamento(int idPagamento) {
        try {
            return pagamentoPixRepository.buscarPorIdPagamento(idPagamento);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar detalhes do Pix: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Pagamento> listarPagamentosPorReserva(int idReserva) {
        try {
            return pagamentoRepository.buscarPorReservaId(idReserva);
        } catch (SQLException e) {
            System.err.println("Erro ao listar pagamentos por reserva: " + e.getMessage());
            return List.of();
        }
    }
}