package com.example.reservas_restaurantes.repository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.reservas_restaurantes.model.Reserva;

public interface ReservaRepository {

    void salvar(Reserva reserva) throws SQLException;

    Optional<Reserva> buscarPorId(int id) throws SQLException;

    List<Reserva> buscarTodos() throws SQLException;

    void atualizar(Reserva reserva) throws SQLException;

    void deletar(int id) throws SQLException;

    List<Reserva> buscarPorData(LocalDate data) throws SQLException;

    List<Reserva> buscarPorCliente(int idCliente) throws SQLException;

    List<Reserva> buscarPorMesaEPeriodo(int idMesa, LocalDateTime inicioPeriodo, LocalDateTime fimPeriodo) throws SQLException;

    List<Reserva> buscarPorMesaEPeriodo(int idMesa, LocalDateTime inicioPeriodoProposto, LocalDateTime fimPeriodoProposto, Integer idReservaExcluir) throws SQLException;

}