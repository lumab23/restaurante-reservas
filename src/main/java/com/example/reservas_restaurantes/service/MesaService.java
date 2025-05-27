package com.example.reservas_restaurantes.service; 

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reservas_restaurantes.enums.StatusMesa;
import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.exception.EntidadeNaoEncontradaException;
import com.example.reservas_restaurantes.model.Mesa;
import com.example.reservas_restaurantes.repository.MesaRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    @Transactional
    public Mesa adicionarMesa(int capacidade, String localizacao) throws BusinessRuleException {
        if (capacidade <= 0) {
            throw new BusinessRuleException("Capacidade da mesa deve ser maior que zero.");
        }
        Mesa novaMesa = new Mesa(capacidade, localizacao, StatusMesa.DISPONIVEL);
        try {
            mesaRepository.salvar(novaMesa);
            return novaMesa;
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao adicionar mesa: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Mesa adicionarMesa(Mesa mesa) throws BusinessRuleException {
        if (mesa.getCapacidade() <= 0) {
            throw new BusinessRuleException("Capacidade da mesa deve ser maior que zero.");
        }
        if(mesa.getStatusMesa() == null){
            mesa.setStatusMesa(StatusMesa.DISPONIVEL);
        }
        try {
            mesaRepository.salvar(mesa);
            return mesa;
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao adicionar mesa: " + e.getMessage(), e);
        }
    }

    public Mesa buscarMesaPorId(int id) throws EntidadeNaoEncontradaException {
        try {
            Optional<Mesa> mesaOpt = mesaRepository.buscarPorId(id);
            if (mesaOpt.isEmpty()) {
                throw new EntidadeNaoEncontradaException("Mesa com ID " + id + " não encontrada.");
            }
            return mesaOpt.get();
        } catch (SQLException e) {
            throw new EntidadeNaoEncontradaException("Erro de banco de dados ao buscar mesa: " + e.getMessage(), e);
        }
    }

    public List<Mesa> listarTodasMesas() {
        try {
            return mesaRepository.buscarTodos();
        } catch (SQLException e) {
            System.err.println("Erro ao listar todas as mesas: " + e.getMessage());
            return List.of();
        }
    }

    @Transactional
    public Mesa atualizarDadosMesa(Mesa mesa) throws BusinessRuleException, EntidadeNaoEncontradaException {
        if (mesa == null || mesa.getIdMesa() == 0) {
            throw new BusinessRuleException("Dados da mesa inválidos para atualização.");
        }
        buscarMesaPorId(mesa.getIdMesa());

        if (mesa.getCapacidade() <= 0) {
            throw new BusinessRuleException("Capacidade da mesa deve ser maior que zero na atualização.");
        }

        try {
            mesaRepository.atualizar(mesa);
            return mesa;
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao atualizar dados da mesa: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void atualizarStatusMesa(int idMesa, StatusMesa novoStatus) throws EntidadeNaoEncontradaException, BusinessRuleException {
        if (novoStatus == null) {
            throw new BusinessRuleException("Novo status da mesa não pode ser nulo.");
        }
        buscarMesaPorId(idMesa);

        try {
            mesaRepository.atualizarStatus(idMesa, novoStatus);
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao atualizar status da mesa: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deletarMesa(int id) throws BusinessRuleException {
        buscarMesaPorId(id);
        try {
            mesaRepository.deletar(id);
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao deletar mesa: " + e.getMessage(), e);
        }
    }
}