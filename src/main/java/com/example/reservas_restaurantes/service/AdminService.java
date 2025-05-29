package com.example.reservas_restaurantes.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.exception.EntidadeNaoEncontradaException;
import com.example.reservas_restaurantes.model.Admin;
import com.example.reservas_restaurantes.repository.AdminRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Transactional
    public Admin cadastrarAdmin(String nome, String email, String senha, String cargo) throws BusinessRuleException {
        validarDadosAdmin(nome, email, senha, cargo);
        try {
            if (adminRepository.buscarPorEmail(email).isPresent()) {
                throw new BusinessRuleException("Email já cadastrado.");
            }
            Admin novoAdmin = new Admin(nome, email, senha, cargo);
            adminRepository.salvar(novoAdmin);
            return novoAdmin;
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao cadastrar administrador: " + e.getMessage(), e);
        }
    }

    public Admin buscarAdminPorId(int id) throws EntidadeNaoEncontradaException {
        try {
            Optional<Admin> adminOpt = adminRepository.buscarPorId(id);
            if (adminOpt.isEmpty()) {
                throw new EntidadeNaoEncontradaException("Administrador com ID " + id + " não encontrado.");
            }
            return adminOpt.get();
        } catch (SQLException e) {
            throw new EntidadeNaoEncontradaException("Erro ao buscar administrador: " + e.getMessage(), e);
        }
    }

    public List<Admin> listarTodosAdmins() throws BusinessRuleException {
        try {
            return adminRepository.buscarTodos();
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao listar administradores: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Admin atualizarAdmin(Admin admin) throws BusinessRuleException, EntidadeNaoEncontradaException {
        validarDadosAdmin(admin.getNome(), admin.getEmail(), admin.getSenha(), admin.getCargo());
        buscarAdminPorId(admin.getIdAdmin());

        try {
            Optional<Admin> adminExistente = adminRepository.buscarPorEmail(admin.getEmail());
            if (adminExistente.isPresent() && adminExistente.get().getIdAdmin() != admin.getIdAdmin()) {
                throw new BusinessRuleException("Email já cadastrado para outro administrador.");
            }
            adminRepository.atualizar(admin);
            return admin;
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao atualizar administrador: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deletarAdmin(int id) throws BusinessRuleException {
        buscarAdminPorId(id);
        try {
            adminRepository.deletar(id);
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao deletar administrador: " + e.getMessage(), e);
        }
    }

    public boolean autenticar(String email, String senha) throws BusinessRuleException {
        try {
            return adminRepository.autenticar(email, senha);
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao autenticar administrador: " + e.getMessage(), e);
        }
    }

    private void validarDadosAdmin(String nome, String email, String senha, String cargo) throws BusinessRuleException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new BusinessRuleException("Nome do administrador não pode ser vazio.");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new BusinessRuleException("Email inválido.");
        }
        if (senha == null || senha.trim().isEmpty() || senha.length() < 6) {
            throw new BusinessRuleException("Senha deve ter pelo menos 6 caracteres.");
        }
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new BusinessRuleException("Cargo do administrador não pode ser vazio.");
        }
    }
} 