package com.example.reservas_restaurantes.controller;

import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.exception.EntidadeNaoEncontradaException;
import com.example.reservas_restaurantes.model.ItemPedido;
import com.example.reservas_restaurantes.model.Pedido;
import com.example.reservas_restaurantes.service.PedidoService;

import java.util.List;

@Component
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public Pedido criarNovoPedido(int idReserva, List<ItemPedido> itens) {
        try {
            Pedido novoPedido = pedidoService.criarNovoPedido(idReserva, itens);
            System.out.println("Controller: Pedido criado com sucesso - ID: " + novoPedido.getIdPedido());
            return novoPedido;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao criar novo pedido - " + e.getMessage());
            return null;
        }
    }

    public Pedido buscarPedidoPorIdComItens(int idPedido) {
        try {
            Pedido pedido = pedidoService.buscarPedidoPorId(idPedido);
            List<ItemPedido> itens = pedidoService.buscarItensPorPedidoId(idPedido);
            System.out.println("Controller: Pedido ID " + idPedido + " encontrado com " + itens.size() + " itens.");
            return pedido;
        } catch (EntidadeNaoEncontradaException e) {
            System.err.println("Controller: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Controller: Erro inesperado ao buscar pedido - " + e.getMessage());
            return null;
        }
    }

    public List<ItemPedido> buscarItensDoPedido(int idPedido) {
        try {
            return pedidoService.buscarItensPorPedidoId(idPedido);
        } catch (Exception e) {
            System.err.println("Controller: Erro ao buscar itens do pedido - " + e.getMessage());
            return List.of();
        }
    }

    public boolean deletarPedido(int idPedido) {
        try {
            pedidoService.deletarPedidoCompleto(idPedido);
            System.out.println("Controller: Pedido ID " + idPedido + " deletado com sucesso.");
            return true;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao deletar pedido - " + e.getMessage());
            return false;
        }
    }
}