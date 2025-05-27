package com.example.reservas_restaurantes.controller;

import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.exception.EntidadeNaoEncontradaException;
import com.example.reservas_restaurantes.model.Cardapio;
import com.example.reservas_restaurantes.service.CardapioService;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CardapioController {

    private final CardapioService cardapioService;

    public CardapioController(CardapioService cardapioService) {
        this.cardapioService = cardapioService;
    }

    public Cardapio adicionarItemCardapio(String nome, String descricao, BigDecimal preco) {
        try {
            Cardapio novoItem = cardapioService.adicionarItemCardapio(nome, descricao, preco);
            System.out.println("Controller: Item do cardápio adicionado com sucesso - ID: " + novoItem.getIdCardapio());
            return novoItem;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao adicionar item ao cardápio - " + e.getMessage());
            return null;
        }
    }

    public Cardapio buscarItemPorId(int id) {
        try {
            Cardapio item = cardapioService.buscarItemPorId(id);
            System.out.println("Controller: Item do cardápio encontrado - ID: " + item.getIdCardapio());
            return item;
        } catch (EntidadeNaoEncontradaException e) {
            System.err.println("Controller: " + e.getMessage());
            return null;
        } catch (Exception e) { // Capturar exceções inesperadas
            System.err.println("Controller: Erro inesperado ao buscar item do cardápio - " + e.getMessage());
            return null;
        }
    }

    public List<Cardapio> listarItensCardapio() {
        try {
            List<Cardapio> itens = cardapioService.listarItensCardapio();
            System.out.println("Controller: " + itens.size() + " itens do cardápio listados.");
            return itens;
        } catch (Exception e) {
            System.err.println("Controller: Erro ao listar itens do cardápio - " + e.getMessage());
            return List.of();
        }
    }

    public boolean atualizarItemCardapio(Cardapio item) {
        try {
            cardapioService.atualizarItemCardapio(item);
            System.out.println("Controller: Item do cardápio ID " + item.getIdCardapio() + " atualizado com sucesso.");
            return true;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao atualizar item do cardápio - " + e.getMessage());
            return false;
        }
    }

    public boolean deletarItemCardapio(int id) {
        try {
            cardapioService.deletarItemCardapio(id);
            System.out.println("Controller: Item do cardápio ID " + id + " deletado com sucesso.");
            return true;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao deletar item do cardápio - " + e.getMessage());
            return false;
        }
    }
}