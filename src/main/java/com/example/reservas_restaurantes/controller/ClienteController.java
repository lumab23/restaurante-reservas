package com.example.reservas_restaurantes.controller;

import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.exception.EntidadeNaoEncontradaException;
import com.example.reservas_restaurantes.model.Cliente;
import com.example.reservas_restaurantes.service.ClienteService;

import java.time.LocalDate;
import java.util.List;

@Component
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    public Cliente cadastrarCliente(String nome, String telefone, String email, LocalDate dataNascimento) {
        try {
            Cliente novoCliente = clienteService.cadastrarCliente(nome, telefone, email, dataNascimento);
            System.out.println("Controller: Cliente cadastrado com sucesso - ID: " + novoCliente.getIdCliente());
            return novoCliente;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao cadastrar cliente - " + e.getMessage());
            return null;
        }
    }

    public Cliente buscarClientePorId(int id) {
        try {
            Cliente cliente = clienteService.buscarClientePorId(id);
            System.out.println("Controller: Cliente encontrado - ID: " + cliente.getIdCliente());
            return cliente;
        } catch (EntidadeNaoEncontradaException e) {
            System.err.println("Controller: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Controller: Erro inesperado ao buscar cliente - " + e.getMessage());
            return null;
        }
    }

    public List<Cliente> listarTodosClientes() {
        try {
            List<Cliente> clientes = clienteService.listarTodosClientes();
            System.out.println("Controller: " + clientes.size() + " clientes listados.");
            return clientes;
        } catch (Exception e) {
            System.err.println("Controller: Erro ao listar clientes - " + e.getMessage());
            return List.of();
        }
    }

    public boolean atualizarCliente(Cliente cliente) {
        try {
            clienteService.atualizarCliente(cliente);
            System.out.println("Controller: Cliente ID " + cliente.getIdCliente() + " atualizado com sucesso.");
            return true;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao atualizar cliente - " + e.getMessage());
            return false;
        }
    }

    public boolean deletarCliente(int id) {
        try {
            clienteService.deletarCliente(id);
            System.out.println("Controller: Cliente ID " + id + " deletado com sucesso.");
            return true;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao deletar cliente - " + e.getMessage());
            return false;
        }
    }
}