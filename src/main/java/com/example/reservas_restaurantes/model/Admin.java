package com.example.reservas_restaurantes.model;

public class Admin extends Pessoa {
    private String senha;
    private String cargo;
    private boolean ativo;

    public Admin() {
        super();
    }

    public Admin(String nome, String email, String senha, String cargo) {
        super(nome, email);
        this.senha = senha;
        this.cargo = cargo;
        this.ativo = true;
    }

    public int getIdAdmin() {
        return getId();
    }

    public void setIdAdmin(int idAdmin) {
        setId(idAdmin);
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return "Admin [idAdmin=" + getId() + ", nome=" + getNome() + ", email=" + getEmail() + 
               ", cargo=" + cargo + ", ativo=" + ativo + "]";
    }
} 