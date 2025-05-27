package com.example.reservas_restaurantes.model;

import java.math.BigDecimal;

public class Cardapio {
    
    private int idCardapio;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    
    public Cardapio() {
    }

    public Cardapio(String nome, String descricao, BigDecimal preco) {
        setNome(nome);
        setDescricao(descricao);
        setPreco(preco);
    }

    public int getIdCardapio() {
        return idCardapio;
    }

    public void setIdCardapio(int idCardapio) {
        if (idCardapio < 0) {
            throw new IllegalArgumentException("ID do cardápio não pode ser negativo");
        }
        this.idCardapio = idCardapio;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser nulo ou vazio");
        }
        this.nome = nome.trim();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode ser nula ou vazia");
        }
        this.descricao = descricao.trim();
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        if (preco == null) {
            throw new IllegalArgumentException("Preço não pode ser nulo");
        }
        if (preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
        this.preco = preco;
    }

    @Override
    public String toString() {
        return "Cardapio [idCardapio=" + idCardapio + ", nome=" + nome + ", descricao=" + descricao + ", preco=" + preco
                + "]";
    }

    

    
} 