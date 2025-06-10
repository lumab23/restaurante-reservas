package com.example.reservas_restaurantes.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.exception.EntidadeNaoEncontradaException;
import com.example.reservas_restaurantes.model.Cliente;
import com.example.reservas_restaurantes.repository.ClienteRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    private void validarIdadeParaReserva(Cliente cliente) throws BusinessRuleException {
        if (cliente.getDataNascimento() == null) {
            throw new BusinessRuleException("A data de nascimento do cliente é obrigatória para verificar a idade.");
        }
        LocalDate hoje = LocalDate.now();
        Period periodo = Period.between(cliente.getDataNascimento(), hoje);
        if (periodo.getYears() < 18) {
            throw new BusinessRuleException("Clientes menores de 18 anos não podem realizar operações restritas. Idade calculada: " + periodo.getYears() + " anos.");
        }
    }

    @Transactional
    public Cliente cadastrarCliente(String nome, String telefone, String email, LocalDate dataNascimento) throws BusinessRuleException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new BusinessRuleException("Nome do cliente não pode ser vazio.");
        }
        Cliente novoCliente = new Cliente(nome, telefone, email, dataNascimento);
        validarIdadeParaReserva(novoCliente);
        try {
            clienteRepository.salvar(novoCliente);
            return novoCliente;
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao cadastrar cliente: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Cliente cadastrarCliente(Cliente cliente) throws BusinessRuleException {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new BusinessRuleException("Nome do cliente não pode ser vazio.");
        }
        validarIdadeParaReserva(cliente);
        try {
            clienteRepository.salvar(cliente);
            return cliente;
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao cadastrar cliente: " + e.getMessage(), e);
        }
    }

    public Cliente buscarClientePorId(int id) throws EntidadeNaoEncontradaException {
        try {
            Optional<Cliente> clienteOpt = clienteRepository.buscarPorId(id);
            if (clienteOpt.isEmpty()) {
                throw new EntidadeNaoEncontradaException("Cliente com ID " + id + " não encontrado.");
            }
            return clienteOpt.get();
        } catch (SQLException e) {
            throw new EntidadeNaoEncontradaException("Erro de banco de dados ao buscar cliente: " + e.getMessage(), e);
        }
    }

    public List<Cliente> listarTodosClientes() {
        try {
            return clienteRepository.buscarTodos();
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os clientes: " + e.getMessage());
            return List.of();
        }
    }

    @Transactional
    public Cliente atualizarCliente(Cliente cliente) throws BusinessRuleException, EntidadeNaoEncontradaException {
        if (cliente == null || cliente.getId() == 0) {
            throw new BusinessRuleException("Dados do cliente inválidos para atualização.");
        }
        buscarClientePorId(cliente.getId());

        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new BusinessRuleException("Nome do cliente não pode ser vazio na atualização.");
        }

        try {
            clienteRepository.atualizar(cliente);
            return cliente;
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deletarCliente(int id) throws BusinessRuleException {
        try {
            // Verificar se o cliente ainda existe antes de tentar deletar
            Optional<Cliente> clienteOpt = clienteRepository.buscarPorId(id);
            if (clienteOpt.isEmpty()) {
                // Se o cliente não existe, não é um erro - apenas retorna silenciosamente
                return;
            }
            
            // Se chegou aqui, o cliente existe e pode ser deletado
            clienteRepository.deletar(id);
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao deletar cliente: " + e.getMessage(), e);
        }
    }

    public Optional<Cliente> buscarClientePorEmail(String email) {
        try {
            return clienteRepository.buscarPorEmail(email);
        } catch (Exception e) {
            System.err.println("Erro ao buscar cliente por email: " + e.getMessage());
            return Optional.empty();
        }
    }
}