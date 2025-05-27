package com.example.reservas_restaurantes.model;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Cliente {
    
    private int idCliente;
    
    private String nome;
    private String telefone;
    private String email;
    private LocalDate dataNascimento;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\(\\d{2}\\) \\d{5}-\\d{4}$");

    public Cliente() {
    }

    public Cliente(String nome, String telefone, String email, LocalDate dataNascimento) {
        setNome(nome);
        setTelefone(telefone);
        setEmail(email);
        setDataNascimento(dataNascimento);
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        if (idCliente < 0) {
            throw new IllegalArgumentException("ID do cliente não pode ser negativo");
        }
        this.idCliente = idCliente;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        if (telefone == null || !PHONE_PATTERN.matcher(telefone).matches()) {
            throw new IllegalArgumentException("Telefone deve estar no formato (XX) XXXXX-XXXX");
        }
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.email = email.toLowerCase();
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalArgumentException("Data de nascimento não pode ser nula");
        }
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode ser no futuro");
        }
        this.dataNascimento = dataNascimento;
    }

    @Override
    public String toString() {
        return "Cliente [idCliente=" + idCliente + ", nome=" + nome + ", telefone=" + telefone + ", email=" + email
                + ", dataNascimento=" + dataNascimento + "]";
    }

    

    
} 