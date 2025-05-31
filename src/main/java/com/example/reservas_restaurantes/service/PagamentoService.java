package com.example.reservas_restaurantes.service; 

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reservas_restaurantes.enums.StatusReserva;
import com.example.reservas_restaurantes.model.Pagamento;
import com.example.reservas_restaurantes.model.PagamentoCartao;
import com.example.reservas_restaurantes.repository.PagamentoCartaoRepository;
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
    private final ReservaRepository reservaRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository,
                            PagamentoCartaoRepository pagamentoCartaoRepository,
                            ReservaRepository reservaRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.pagamentoCartaoRepository = pagamentoCartaoRepository;
        this.reservaRepository = reservaRepository;
    }

    @Transactional
    public Pagamento processarPagamento(int idReserva, BigDecimal valor, PagamentoCartao detalhesCartao)
            throws SQLException {
        // Validar se a reserva existe e está confirmada
        var reserva = reservaRepository.buscarPorId(idReserva)
            .orElseThrow(() -> new SQLException("Reserva não encontrada"));
            
        if (reserva.getStatusReserva() != StatusReserva.CONFIRMADA) {
            throw new SQLException("Apenas reservas confirmadas podem ser pagas");
        }

        // Criar o pagamento
        Pagamento pagamento = new Pagamento();
        pagamento.setIdReserva(idReserva);
        pagamento.setValor(valor);
        pagamento.setMetodoPagamento("CARTAO"); 

        // Salvar o pagamento
        pagamentoRepository.salvar(pagamento);

        // Salvar detalhes do cartão
        if (detalhesCartao != null) {
            detalhesCartao.setIdPagamento(pagamento.getIdPagamento());
            pagamentoCartaoRepository.salvar(detalhesCartao);
        }

        return pagamento;
    }

    public Optional<Pagamento> buscarPagamentoPorId(int idPagamento) throws SQLException {
        return pagamentoRepository.buscarPorId(idPagamento);
    }

    public Optional<PagamentoCartao> buscarDetalhesCartaoPorIdPagamento(int idPagamento) {
        try {
            return pagamentoCartaoRepository.buscarPorIdPagamento(idPagamento);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar detalhes do cartão", e);
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