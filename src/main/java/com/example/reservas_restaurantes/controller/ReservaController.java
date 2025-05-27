package com.example.reservas_restaurantes.controller;

import org.springframework.stereotype.Component;

import com.example.reservas_restaurantes.enums.TipoOcasiao;
import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.exception.EntidadeNaoEncontradaException;
import com.example.reservas_restaurantes.model.Reserva;
import com.example.reservas_restaurantes.service.ReservaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    public Reserva fazerNovaReserva(int idCliente, int idMesa, LocalDateTime dataHora, int numPessoas, TipoOcasiao ocasiao, String observacao) {
        try {
            Reserva novaReserva = reservaService.fazerNovaReserva(idCliente, idMesa, dataHora, numPessoas, ocasiao, observacao);
            System.out.println("Controller: Reserva criada com sucesso - ID: " + novaReserva.getIdReserva());
            return novaReserva;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao fazer nova reserva - " + e.getMessage());
            return null;
        }
    }

    public List<LocalTime> getHorariosDisponiveis(LocalDate dia, int numPessoas, Integer idMesaOpcional) {
        try {
            List<LocalTime> horarios = reservaService.getHorariosDisponiveis(dia, numPessoas, idMesaOpcional);
            System.out.println("Controller: Horários disponíveis para " + dia + ": " + horarios);
            return horarios;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao buscar horários disponíveis - " + e.getMessage());
            return List.of();
        }
    }

    public Reserva buscarReservaPorId(int id) {
        try {
            Reserva reserva = reservaService.buscarReservaPorId(id);
            System.out.println("Controller: Reserva encontrada - ID: " + reserva.getIdReserva());
            return reserva;
        } catch (EntidadeNaoEncontradaException e) {
            System.err.println("Controller: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Controller: Erro inesperado ao buscar reserva - " + e.getMessage());
            return null;
        }
    }

    public List<Reserva> listarReservasPorData(LocalDate data) {
        try {
            List<Reserva> reservas = reservaService.listarReservasPorData(data);
            System.out.println("Controller: " + reservas.size() + " reservas encontradas para a data " + data);
            return reservas;
        } catch (Exception e) {
            System.err.println("Controller: Erro ao listar reservas por data: " + e.getMessage());
            return List.of();
        }
    }

    public boolean cancelarReserva(int idReserva) {
        try {
            reservaService.cancelarReserva(idReserva);
            System.out.println("Controller: Reserva ID " + idReserva + " cancelada com sucesso.");
            return true;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao cancelar reserva - " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarReserva(Reserva reserva) {
        try {
            reservaService.atualizarReserva(reserva);
            System.out.println("Controller: Reserva ID " + reserva.getIdReserva() + " atualizada com sucesso.");
            return true;
        } catch (BusinessRuleException e) {
            System.err.println("Controller: Erro ao atualizar reserva - " + e.getMessage());
            return false;
        }
    }
}