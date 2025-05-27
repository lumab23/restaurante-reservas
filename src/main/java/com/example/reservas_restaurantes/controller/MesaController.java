package com.example.reservas_restaurantes.controller;


import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.enums.StatusMesa;
import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.exception.EntidadeNaoEncontradaException;
import com.example.reservas_restaurantes.model.Mesa;
import com.example.reservas_restaurantes.service.MesaService;

import java.util.List;

@Component
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    public Mesa adicionarMesa(int capacidade, String localizacao) {
        try {
            Mesa novaMesa = mesaService.adicionarMesa(capacidade, localizacao);
            System.out.println("Controller: Mesa adicionada com sucesso - ID: " + novaMesa.getIdMesa());
            return novaMesa;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao adicionar mesa - " + e.getMessage());
            return null;
        }
    }

    public Mesa buscarMesaPorId(int id) {
        try {
            Mesa mesa = mesaService.buscarMesaPorId(id);
            System.out.println("Controller: Mesa encontrada - ID: " + mesa.getIdMesa());
            return mesa;
        } catch (EntidadeNaoEncontradaException e) {
            System.err.println("Controller: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Controller: Erro inesperado ao buscar mesa - " + e.getMessage());
            return null;
        }
    }

    public List<Mesa> listarTodasMesas() {
        try {
            List<Mesa> mesas = mesaService.listarTodasMesas();
            System.out.println("Controller: " + mesas.size() + " mesas listadas.");
            return mesas;
        } catch (Exception e) {
            System.err.println("Controller: Erro ao listar mesas - " + e.getMessage());
            return List.of();
        }
    }

    public boolean atualizarDadosMesa(Mesa mesa) {
        try {
            mesaService.atualizarDadosMesa(mesa);
            System.out.println("Controller: Mesa ID " + mesa.getIdMesa() + " atualizada com sucesso.");
            return true;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao atualizar dados da mesa - " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarStatusMesa(int idMesa, StatusMesa novoStatus) {
        try {
            mesaService.atualizarStatusMesa(idMesa, novoStatus);
            System.out.println("Controller: Status da mesa ID " + idMesa + " atualizado para " + novoStatus);
            return true;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao atualizar status da mesa - " + e.getMessage());
            return false;
        }
    }

    public boolean deletarMesa(int id) {
        try {
            mesaService.deletarMesa(id);
            System.out.println("Controller: Mesa ID " + id + " deletada com sucesso.");
            return true;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao deletar mesa - " + e.getMessage());
            return false;
        }
    }
}