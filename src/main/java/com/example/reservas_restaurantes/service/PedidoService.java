package com.example.reservas_restaurantes.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.exception.EntidadeNaoEncontradaException;
import com.example.reservas_restaurantes.model.Cardapio;
import com.example.reservas_restaurantes.model.ItemPedido;
import com.example.reservas_restaurantes.model.Pedido;
import com.example.reservas_restaurantes.model.Reserva;
import com.example.reservas_restaurantes.repository.CardapioRepository;
import com.example.reservas_restaurantes.repository.ItemPedidoRepository;
import com.example.reservas_restaurantes.repository.PedidoRepository;
import com.example.reservas_restaurantes.repository.ReservaRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final ReservaRepository reservaRepository;
    private final CardapioRepository cardapioRepository;

    public PedidoService(PedidoRepository pedidoRepository, ItemPedidoRepository itemPedidoRepository,
                         ReservaRepository reservaRepository, CardapioRepository cardapioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.reservaRepository = reservaRepository;
        this.cardapioRepository = cardapioRepository;
    }

    @Transactional
    public Pedido criarNovoPedido(int idReserva, List<ItemPedido> itens) throws BusinessRuleException, EntidadeNaoEncontradaException {
        // 1. Validar Reserva
        try {
            Optional<Reserva> reservaOpt = reservaRepository.buscarPorId(idReserva);
            if (reservaOpt.isEmpty()) {
                throw new EntidadeNaoEncontradaException("Reserva com ID " + idReserva + " não encontrada para criar o pedido.");
            }
        } catch (SQLException e) {
            throw new EntidadeNaoEncontradaException("Erro de banco de dados ao buscar reserva: " + e.getMessage(), e);
        }

        if (itens == null || itens.isEmpty()) {
            throw new BusinessRuleException("Pedido deve conter pelo menos um item.");
        }

        // 2. Validar Itens do Cardápio e Quantidades
        try {
            for (ItemPedido item : itens) {
                if (item.getQuantidade() <= 0) {
                    throw new BusinessRuleException("Quantidade do item do cardápio deve ser positiva.");
                }
                Optional<Cardapio> cardapioItemOpt = cardapioRepository.buscarPorId(item.getIdCardapio());
                if (cardapioItemOpt.isEmpty()) {
                    throw new EntidadeNaoEncontradaException("Item do cardápio com ID " + item.getIdCardapio() + " não encontrado.");
                }
            }
        } catch (SQLException e) {
            throw new EntidadeNaoEncontradaException("Erro de banco de dados ao validar itens do cardápio: " + e.getMessage(), e);
        }


        // 3. Criar e Salvar Pedido
        Pedido novoPedido = new Pedido(idReserva, LocalDateTime.now());
        try {
            pedidoRepository.salvar(novoPedido);
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao salvar pedido principal: " + e.getMessage(), e);
        }


        // 4. Salvar Itens do Pedido
        try {
            for (ItemPedido item : itens) {
                item.setIdPedido(novoPedido.getIdPedido());
                itemPedidoRepository.salvar(item);
            }
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao salvar itens do pedido: " + e.getMessage(), e);
        }
        return novoPedido;
    }

    public Pedido buscarPedidoPorId(int idPedido) throws EntidadeNaoEncontradaException {
        try {
            Optional<Pedido> pedidoOpt = pedidoRepository.buscarPorId(idPedido);
            if (pedidoOpt.isEmpty()) {
                throw new EntidadeNaoEncontradaException("Pedido com ID " + idPedido + " não encontrado.");
            }
            return pedidoOpt.get();
        } catch (SQLException e) {
            throw new EntidadeNaoEncontradaException("Erro de banco de dados ao buscar pedido: " + e.getMessage(), e);
        }
    }

    public List<ItemPedido> buscarItensPorPedidoId(int idPedido) {
        try {
            return itemPedidoRepository.buscarPorPedidoId(idPedido);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar itens por ID do pedido: " + e.getMessage());
            return List.of();
        }
    }

    public List<Pedido> listarPedidosPorReserva(int idReserva) {
        try {
            return pedidoRepository.buscarPorReserva(idReserva);
        } catch (SQLException e) {
            System.err.println("Erro ao listar pedidos por reserva: " + e.getMessage());
            return List.of();
        }
    }

    @Transactional
    public void deletarPedidoCompleto(int idPedido) throws BusinessRuleException {
        buscarPedidoPorId(idPedido);

        try {
            // Deletar itens do pedido primeiro, se ON DELETE CASCADE não for suficiente
            itemPedidoRepository.deletarPorPedidoId(idPedido); // Mantenha isso se a lógica do DB não for CASCADE
            pedidoRepository.deletar(idPedido);
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao deletar pedido: " + e.getMessage(), e);
        }
    }
}