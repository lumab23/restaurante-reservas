package com.example.reservas_restaurantes.model;

public class ItemPedido {

    private int idItemPedido;
    private int idPedido;
    private int idCardapio;
    private int quantidade;

    public ItemPedido() {
    }

    public ItemPedido(int idPedido, int idCardapio, int quantidade) {
        setIdPedido(idPedido);
        setIdCardapio(idCardapio);
        setQuantidade(quantidade);
    }

    public int getIdItemPedido() {
        return idItemPedido;
    }

    public void setIdItemPedido(int idItemPedido) {
        if (idItemPedido < 0) {
            throw new IllegalArgumentException("ID do item do pedido não pode ser negativo");
        }
        this.idItemPedido = idItemPedido;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        if (idPedido <= 0) {
            throw new IllegalArgumentException("ID do pedido deve ser maior que zero");
        }
        this.idPedido = idPedido;
    }

    public int getIdCardapio() {
        return idCardapio;
    }

    public void setIdCardapio(int idCardapio) {
        if (idCardapio <= 0) {
            throw new IllegalArgumentException("ID do cardápio deve ser maior que zero");
        }
        this.idCardapio = idCardapio;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return "ItemPedido [idItemPedido=" + idItemPedido + ", idPedido=" + idPedido + ", idCardapio=" + idCardapio
                + ", quantidade=" + quantidade + "]";
    }

    


    
} 