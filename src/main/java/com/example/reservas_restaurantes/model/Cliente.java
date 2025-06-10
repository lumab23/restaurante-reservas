package com.example.reservas_restaurantes.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class Cliente extends Pessoa {
    private String telefone;
    private LocalDate dataNascimento;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\(\\d{2}\\) \\d{5}-\\d{4}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private boolean isNewClient = true;

    public Cliente() {
        super();
        this.isNewClient = false;
    }

    public Cliente(String nome, String telefone, String email, LocalDate dataNascimento) {
        super(nome, email);
        this.isNewClient = true;
        setTelefone(telefone);
        setDataNascimento(dataNascimento);
    }

    public int getIdCliente() {
        return getId();
    }

    public void setIdCliente(int idCliente) {
        setId(idCliente);
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        if (telefone == null) {
            throw new IllegalArgumentException("Telefone não pode ser nulo");
        }
        if (isNewClient && !PHONE_PATTERN.matcher(telefone).matches()) {
            throw new IllegalArgumentException("Telefone deve estar no formato (XX) XXXXX-XXXX");
        }
        this.telefone = telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new IllegalArgumentException("Data de nascimento não pode ser nula");
        }
        if (isNewClient && dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode ser no futuro");
        }
        this.dataNascimento = dataNascimento;
    }

    @Override
    public String toString() {
        return String.format("%s (Nasc: %s) - Tel: %s - Email: %s",
            getNome(),
            dataNascimento != null ? dataNascimento.format(DATE_FORMATTER) : "N/A",
            telefone,
            getEmail());
    }
} 