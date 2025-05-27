package com.example.reservas_restaurantes.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.exception.EntidadeNaoEncontradaException;
import com.example.reservas_restaurantes.model.Cardapio;
import com.example.reservas_restaurantes.repository.CardapioRepository;

import java.math.BigDecimal;
import java.sql.SQLException; // A SQL Exception pode ser lançada pelos DAOs
import java.util.List;
import java.util.Optional;

@Service
public class CardapioService {

    private final CardapioRepository cardapioRepository;

    public CardapioService(CardapioRepository cardapioRepository) {
        this.cardapioRepository = cardapioRepository;
    }

    @Transactional
    public Cardapio adicionarItemCardapio(String nome, String descricao, BigDecimal preco) throws BusinessRuleException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new BusinessRuleException("Nome do item do cardápio não pode ser vazio.");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("Preço do item do cardápio deve ser um valor positivo ou zero.");
        }
        Cardapio novoItem = new Cardapio(nome, descricao, preco);
        try {
            cardapioRepository.salvar(novoItem);
            return novoItem;
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao salvar item do cardápio: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Cardapio adicionarItemCardapio(Cardapio item) throws BusinessRuleException {
        if (item.getNome() == null || item.getNome().trim().isEmpty()) {
            throw new BusinessRuleException("Nome do item do cardápio não pode ser vazio.");
        }
        if (item.getPreco() == null || item.getPreco().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("Preço do item do cardápio deve ser um valor positivo ou zero.");
        }
        try {
            cardapioRepository.salvar(item);
            return item;
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao salvar item do cardápio: " + e.getMessage(), e);
        }
    }

    public Cardapio buscarItemPorId(int id) throws EntidadeNaoEncontradaException {
        try {
            Optional<Cardapio> itemOpt = cardapioRepository.buscarPorId(id);
            if (itemOpt.isEmpty()) {
                throw new EntidadeNaoEncontradaException("Item do cardápio com ID " + id + " não encontrado.");
            }
            return itemOpt.get();
        } catch (SQLException e) {
            throw new EntidadeNaoEncontradaException("Erro de banco de dados ao buscar item do cardápio: " + e.getMessage(), e);
        }
    }

    public List<Cardapio> listarItensCardapio() {
        try {
            return cardapioRepository.buscarTodos();
        } catch (SQLException e) {
            System.err.println("Erro ao listar itens do cardápio: " + e.getMessage());
            return List.of();
        }
    }

    @Transactional
    public Cardapio atualizarItemCardapio(Cardapio item) throws BusinessRuleException, EntidadeNaoEncontradaException {
        if (item == null || item.getIdCardapio() == 0) {
            throw new BusinessRuleException("Dados do item do cardápio inválidos para atualização.");
        }
        buscarItemPorId(item.getIdCardapio()); // Valida se o item existe

        if (item.getNome() == null || item.getNome().trim().isEmpty()) {
            throw new BusinessRuleException("Nome do item do cardápio não pode ser vazio na atualização.");
        }
        if (item.getPreco() == null || item.getPreco().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("Preço do item do cardápio deve ser um valor positivo ou zero na atualização.");
        }

        try {
            cardapioRepository.atualizar(item);
            return item;
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao atualizar item do cardápio: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deletarItemCardapio(int id) throws BusinessRuleException {
        buscarItemPorId(id); // Valida se o item existe
        try {
            cardapioRepository.deletar(id);
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao deletar item do cardápio: " + e.getMessage(), e);
        }
    }
}