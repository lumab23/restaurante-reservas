package com.example.reservas_restaurantes.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reservas_restaurantes.enums.StatusMesa;
import com.example.reservas_restaurantes.enums.StatusReserva;
import com.example.reservas_restaurantes.enums.TipoOcasiao;
import com.example.reservas_restaurantes.exception.BusinessRuleException;
import com.example.reservas_restaurantes.exception.EntidadeNaoEncontradaException;
import com.example.reservas_restaurantes.model.Cliente;
import com.example.reservas_restaurantes.model.Mesa;
import com.example.reservas_restaurantes.model.Reserva;
import com.example.reservas_restaurantes.repository.ClienteRepository;
import com.example.reservas_restaurantes.repository.MesaRepository;
import com.example.reservas_restaurantes.repository.ReservaRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final MesaRepository mesaRepository;

    public static final LocalTime HORA_ABERTURA_RESTAURANTE = LocalTime.of(12, 0);
    public static final LocalTime HORA_FECHAMENTO_RESTAURANTE = LocalTime.of(23, 0);
    public static final int DURACAO_PADRAO_RESERVA_HORAS = 2;
    private static final int INTERVALO_SLOTS_MINUTOS = 30;

    public ReservaService(ReservaRepository reservaRepository,
                         ClienteRepository clienteRepository,
                         MesaRepository mesaRepository) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.mesaRepository = mesaRepository;
    }

    private void validarIdadeClienteParaReserva(Cliente cliente) throws BusinessRuleException {
        if (cliente.getDataNascimento() == null) {
            throw new BusinessRuleException("A data de nascimento do cliente é obrigatória para fazer uma reserva.");
        }
        LocalDate hoje = LocalDate.now();
        Period periodo = Period.between(cliente.getDataNascimento(), hoje);
        if (periodo.getYears() < 18) {
            throw new BusinessRuleException("Clientes menores de 18 anos não podem fazer reservas. Idade: " + periodo.getYears());
        }
    }

    @Transactional(rollbackFor = {SQLException.class, BusinessRuleException.class, EntidadeNaoEncontradaException.class})
    public Reserva fazerNovaReserva(int idCliente, int idMesa, LocalDateTime dataHora, int numPessoas, TipoOcasiao ocasiao, String observacao)
            throws BusinessRuleException, EntidadeNaoEncontradaException {

        try {
            Optional<Cliente> clienteOpt = clienteRepository.buscarPorId(idCliente);
            if (clienteOpt.isEmpty()) {
                throw new EntidadeNaoEncontradaException("Cliente com ID " + idCliente + " não encontrado.");
            }
            validarIdadeClienteParaReserva(clienteOpt.get());

            Optional<Mesa> mesaOpt = mesaRepository.buscarPorId(idMesa);
            if (mesaOpt.isEmpty()) {
                throw new EntidadeNaoEncontradaException("Mesa com ID " + idMesa + " não encontrada.");
            }
            Mesa mesa = mesaOpt.get();

            if (numPessoas <= 0) {
                throw new BusinessRuleException("Número de pessoas deve ser maior que zero.");
            }
            if (numPessoas > mesa.getCapacidade()) {
                throw new BusinessRuleException("Número de pessoas (" + numPessoas + ") excede a capacidade da mesa (" + mesa.getCapacidade() + ").");
            }

            if (dataHora.isBefore(LocalDateTime.now())) {
                throw new BusinessRuleException("Não é possível fazer reservas para datas ou horários passados.");
            }
            if (dataHora.toLocalTime().isBefore(HORA_ABERTURA_RESTAURANTE) ||
                    dataHora.toLocalTime().plusHours(DURACAO_PADRAO_RESERVA_HORAS).isAfter(HORA_FECHAMENTO_RESTAURANTE.plusMinutes(1))) {
                throw new BusinessRuleException("Horário da reserva fora do horário de funcionamento do restaurante (" +
                        HORA_ABERTURA_RESTAURANTE + " - " + HORA_FECHAMENTO_RESTAURANTE + ").");
            }

            LocalDateTime fimPropostoReserva = dataHora.plusHours(DURACAO_PADRAO_RESERVA_HORAS);
            List<Reserva> conflitos = reservaRepository.buscarPorMesaEPeriodo(idMesa, dataHora, fimPropostoReserva);
            if (!conflitos.isEmpty()) {
                throw new BusinessRuleException("Mesa " + idMesa + " já está reservada ou ocupada neste horário.");
            }

            Reserva novaReserva = new Reserva(idCliente, idMesa, dataHora, numPessoas, ocasiao, StatusReserva.CONFIRMADA, observacao);
            reservaRepository.salvar(novaReserva);

            mesaRepository.atualizarStatus(idMesa, StatusMesa.RESERVADA);

            return novaReserva;
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro de banco de dados ao fazer nova reserva: " + e.getMessage(), e);
        }
    }

    public List<LocalTime> getHorariosDisponiveis(LocalDate dia, int numPessoas, Integer idMesaOpcional) throws BusinessRuleException, EntidadeNaoEncontradaException {
        List<LocalTime> horariosDisponiveis = new ArrayList<>();
        List<Mesa> mesasConsideradas;

        try {
            if (idMesaOpcional != null) {
                Optional<Mesa> mesaOpt = mesaRepository.buscarPorId(idMesaOpcional);
                if(mesaOpt.isEmpty()){
                    throw new EntidadeNaoEncontradaException("Mesa com ID " + idMesaOpcional + " não encontrada.");
                }
                Mesa mesa = mesaOpt.get();
                if(mesa.getCapacidade() < numPessoas){
                    throw new BusinessRuleException("Mesa selecionada não comporta " + numPessoas + " pessoas.");
                }
                mesasConsideradas = List.of(mesa);
            } else {
                mesasConsideradas = mesaRepository.buscarTodos().stream()
                        .filter(m -> m.getCapacidade() >= numPessoas)
                        .collect(Collectors.toList());
            }
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro de banco de dados ao buscar mesas: " + e.getMessage(), e);
        }

        if (mesasConsideradas.isEmpty()){
            throw new BusinessRuleException("Nenhuma mesa encontrada com capacidade para " + numPessoas + " pessoas.");
        }

        LocalTime slotAtual = HORA_ABERTURA_RESTAURANTE;
        while (!slotAtual.plusHours(DURACAO_PADRAO_RESERVA_HORAS).isAfter(HORA_FECHAMENTO_RESTAURANTE.plusMinutes(1))) {
            LocalDateTime inicioSlotProposto = LocalDateTime.of(dia, slotAtual);
            LocalDateTime fimSlotProposto = inicioSlotProposto.plusHours(DURACAO_PADRAO_RESERVA_HORAS);

            if (inicioSlotProposto.isAfter(LocalDateTime.now())) {
                boolean slotDisponivelEmAlgumaMesa = false;
                try {
                    for (Mesa mesa : mesasConsideradas) {
                        List<Reserva> conflitos = reservaRepository.buscarPorMesaEPeriodo(mesa.getIdMesa(), inicioSlotProposto, fimSlotProposto);
                        if (conflitos.isEmpty()) {
                            slotDisponivelEmAlgumaMesa = true;
                            break;
                        }
                    }
                } catch (SQLException e) {
                    throw new BusinessRuleException("Erro de banco de dados ao verificar conflitos de reserva: " + e.getMessage(), e);
                }

                if (slotDisponivelEmAlgumaMesa) {
                    horariosDisponiveis.add(slotAtual);
                }
            }
            slotAtual = slotAtual.plusMinutes(INTERVALO_SLOTS_MINUTOS);
        }
        return horariosDisponiveis;
    }

    public Reserva buscarReservaPorId(int id) throws EntidadeNaoEncontradaException {
        try {
            Optional<Reserva> reservaOpt = reservaRepository.buscarPorId(id);
            if (reservaOpt.isEmpty()) {
                throw new EntidadeNaoEncontradaException("Reserva com ID " + id + " não encontrada.");
            }
            return reservaOpt.get();
        } catch (SQLException e) {
            throw new EntidadeNaoEncontradaException("Erro de banco de dados ao buscar reserva: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza o status de todas as mesas baseado nas reservas ativas.
     * Mesas sem reservas ativas são marcadas como DISPONIVEL.
     */
    @Transactional
    public void atualizarStatusTodasMesas() {
        try {
            System.out.println("Iniciando atualização de status de todas as mesas...");
            List<Mesa> todasMesas = mesaRepository.buscarTodos();
            LocalDateTime agora = LocalDateTime.now();
            LocalDateTime inicioPeriodo = agora.minusHours(DURACAO_PADRAO_RESERVA_HORAS);
            // Definir um período de 30 dias para frente para verificar reservas ativas
            LocalDateTime fimPeriodo = agora.plusDays(30);
            
            for (Mesa mesa : todasMesas) {
                try {
                    List<Reserva> reservasAtivas = reservaRepository
                        .buscarPorMesaEPeriodo(mesa.getIdMesa(), inicioPeriodo, fimPeriodo)
                        .stream()
                        .filter(r -> r.getStatusReserva() != StatusReserva.CANCELADA)
                        .collect(Collectors.toList());

                    if (reservasAtivas.isEmpty()) {
                        if (mesa.getStatusMesa() != StatusMesa.DISPONIVEL) {
                            mesaRepository.atualizarStatus(mesa.getIdMesa(), StatusMesa.DISPONIVEL);
                            System.out.println("Mesa ID " + mesa.getIdMesa() + " atualizada para DISPONIVEL (sem reservas ativas)");
                        }
                    } else {
                        // Se tem reservas ativas, mantém como RESERVADA
                        if (mesa.getStatusMesa() != StatusMesa.RESERVADA) {
                            mesaRepository.atualizarStatus(mesa.getIdMesa(), StatusMesa.RESERVADA);
                            System.out.println("Mesa ID " + mesa.getIdMesa() + " atualizada para RESERVADA (com " + reservasAtivas.size() + " reservas ativas)");
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Erro ao atualizar status da mesa ID " + mesa.getIdMesa() + ": " + e.getMessage());
                }
            }
            System.out.println("Atualização de status de todas as mesas concluída.");
        } catch (SQLException e) {
            System.err.println("Erro ao buscar mesas para atualização de status: " + e.getMessage());
        }
    }

    public List<Reserva> listarTodasReservas() {
        try {
            System.out.println("ReservaService: Iniciando busca de todas as reservas");
            List<Reserva> reservas = reservaRepository.buscarTodos();
            System.out.println("ReservaService: Encontradas " + reservas.size() + " reservas");
            
            // Atualiza o status das mesas após listar as reservas
            atualizarStatusTodasMesas();
            
            return reservas;
        } catch (SQLException e) {
            System.err.println("Erro detalhado ao listar todas as reservas: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    public List<Reserva> listarReservasPorData(LocalDate data) {
        try {
            return reservaRepository.buscarPorData(data);
        } catch (SQLException e) {
            System.err.println("Erro ao listar reservas por data: " + e.getMessage());
            return List.of();
        }
    }

    public List<Reserva> listarReservasPorCliente(int idCliente) {
        try {
            return reservaRepository.buscarPorCliente(idCliente);
        } catch (SQLException e) {
            System.err.println("Erro ao listar reservas por cliente: " + e.getMessage());
            return List.of();
        }
    }

    @Transactional
    public Reserva atualizarReserva(Reserva reserva) throws BusinessRuleException, EntidadeNaoEncontradaException {
        if (reserva == null || reserva.getIdReserva() == 0) {
            throw new BusinessRuleException("Dados da reserva inválidos para atualização.");
        }
        buscarReservaPorId(reserva.getIdReserva());

        try {
            Optional<Cliente> clienteOpt = clienteRepository.buscarPorId(reserva.getIdCliente());
            if (clienteOpt.isEmpty()) throw new EntidadeNaoEncontradaException("Cliente com ID " + reserva.getIdCliente() + " não encontrado para atualização da reserva.");
            validarIdadeClienteParaReserva(clienteOpt.get());

            Optional<Mesa> mesaOpt = mesaRepository.buscarPorId(reserva.getIdMesa());
            if (mesaOpt.isEmpty()) throw new EntidadeNaoEncontradaException("Mesa com ID " + reserva.getIdMesa() + " não encontrada para atualização da reserva.");
            if (reserva.getNumPessoas() > mesaOpt.get().getCapacidade()) {
                throw new BusinessRuleException("Número de pessoas excede a capacidade da mesa selecionada.");
            }

            LocalDateTime fimPropostoReserva = reserva.getDataHora().plusHours(DURACAO_PADRAO_RESERVA_HORAS);
            List<Reserva> conflitos = reservaRepository.buscarPorMesaEPeriodo(reserva.getIdMesa(), reserva.getDataHora(), fimPropostoReserva);
            for (Reserva conflito : conflitos) {
                if (conflito.getIdReserva() != reserva.getIdReserva()) {
                    throw new BusinessRuleException("Mesa " + reserva.getIdMesa() + " já está reservada ou ocupada neste horário (conflito ao atualizar).");
                }
            }

            reservaRepository.atualizar(reserva);
            return reserva;
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro de banco de dados ao atualizar reserva: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void cancelarReserva(int idReserva) throws EntidadeNaoEncontradaException, BusinessRuleException {
        Reserva reserva = buscarReservaPorId(idReserva);
        if (reserva.getStatusReserva() == StatusReserva.CANCELADA) {
            throw new BusinessRuleException("Reserva já está cancelada.");
        }

        reserva.setStatusReserva(StatusReserva.CANCELADA);
        try {
            reservaRepository.atualizar(reserva);
        } catch (SQLException e) {
            throw new BusinessRuleException("Erro ao cancelar reserva: " + e.getMessage(), e);
        }

        // Lógica para verificar se a mesa pode voltar a ser DISPONIVEL
        try {
            List<Reserva> outrasReservasAtivasParaMesa = reservaRepository
                .buscarPorMesaEPeriodo(reserva.getIdMesa(), LocalDateTime.now().minusHours(DURACAO_PADRAO_RESERVA_HORAS), LocalDateTime.MAX)
                .stream()
                .filter(r -> r.getIdReserva() != idReserva && r.getStatusReserva() != StatusReserva.CANCELADA)
                .collect(Collectors.toList());

            if (outrasReservasAtivasParaMesa.isEmpty()) {
                mesaRepository.atualizarStatus(reserva.getIdMesa(), StatusMesa.DISPONIVEL);
                System.out.println("Reserva ID " + idReserva + " cancelada. Mesa ID " + reserva.getIdMesa() + " definida como DISPONIVEL.");
            } else {
                System.out.println("Reserva ID " + idReserva + " cancelada. Mesa ID " + reserva.getIdMesa() + " permanece RESERVADA/OCUPADA devido a outras reservas ativas.");
            }
        } catch (SQLException e) {
            System.err.println("Atenção: Reserva cancelada, mas falha ao verificar status da mesa: " + e.getMessage());
        }
    }

    // deletar reserva
    @Transactional(rollbackFor = {SQLException.class, BusinessRuleException.class, EntidadeNaoEncontradaException.class})
    public void deletarReserva(int idReserva) throws BusinessRuleException, EntidadeNaoEncontradaException {
        System.out.println("Iniciando deleção da reserva ID: " + idReserva);
        
        // Buscar a reserva antes de deletar para ter acesso aos dados
        Reserva reserva = buscarReservaPorId(idReserva);
        int idMesa = reserva.getIdMesa();
        System.out.println("Reserva encontrada - Mesa ID: " + idMesa);
        
        try {
            // Deletar a reserva
            reservaRepository.deletar(idReserva);
            System.out.println("Reserva ID " + idReserva + " deletada com sucesso");

            // Atualizar status de todas as mesas após deletar a reserva
            atualizarStatusTodasMesas();

            // Verificar se o cliente tem outras reservas
            List<Reserva> outrasReservasCliente = reservaRepository.buscarPorCliente(reserva.getIdCliente())
                .stream()
                .filter(r -> r.getIdReserva() != idReserva)
                .collect(Collectors.toList());

            if (outrasReservasCliente.isEmpty()) {
                System.out.println("Cliente ID " + reserva.getIdCliente() + " não possui outras reservas. Deletando cliente...");
                clienteRepository.deletar(reserva.getIdCliente());
                System.out.println("Cliente ID " + reserva.getIdCliente() + " deletado com sucesso.");
            } else {
                System.out.println("Cliente ID " + reserva.getIdCliente() + " possui " + outrasReservasCliente.size() + " outras reservas. Mantendo cliente.");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao deletar reserva: " + e.getMessage());
            throw new BusinessRuleException("Erro ao deletar reserva: " + e.getMessage(), e);
        }
    }
}