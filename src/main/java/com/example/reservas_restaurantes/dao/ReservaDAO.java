package com.example.reservas_restaurantes.dao;

import org.springframework.stereotype.Repository;

import com.example.reservas_restaurantes.enums.StatusReserva;
import com.example.reservas_restaurantes.enums.TipoOcasiao;
import com.example.reservas_restaurantes.model.Reserva;
import com.example.reservas_restaurantes.repository.ReservaRepository;

import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservaDAO implements ReservaRepository {

    private final DataSource dataSource;

    public ReservaDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void releaseConnection(Connection conn) {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    @Override
    public void salvar(Reserva reserva) throws SQLException {
        String sql = "INSERT INTO Reserva (id_cliente, id_mesa, dataHora, numPessoas, ocasiao, statusReserva, observacao) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet chavesGeradas = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, reserva.getIdCliente());
            statement.setInt(2, reserva.getIdMesa());
            statement.setTimestamp(3, Timestamp.valueOf(reserva.getDataHora()));
            statement.setInt(4, reserva.getNumPessoas());
            statement.setString(5, reserva.getOcasiao().name());
            statement.setString(6, reserva.getStatusReserva().name());
            statement.setString(7, reserva.getObservacao());

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao criar reserva, nenhuma linha afetada.");
            }

            chavesGeradas = statement.getGeneratedKeys();
            if (chavesGeradas.next()) {
                reserva.setIdReserva(chavesGeradas.getInt(1));
            } else {
                throw new SQLException("Falha ao criar reserva, nenhum ID obtido.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar reserva: " + e.getMessage());
            throw e;
        } finally {
            if (chavesGeradas != null) chavesGeradas.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
    }

    @Override
    public Optional<Reserva> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id_reserva, id_cliente, id_mesa, dataHora, numPessoas, ocasiao, statusReserva, observacao " +
                "FROM Reserva WHERE id_reserva = ?";
        Reserva reserva = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            if (rs.next()) {
                reserva = new Reserva();
                reserva.setIdReserva(rs.getInt("id_reserva"));
                reserva.setIdCliente(rs.getInt("id_cliente"));
                reserva.setIdMesa(rs.getInt("id_mesa"));
                reserva.setDataHora(rs.getTimestamp("dataHora").toLocalDateTime());
                reserva.setNumPessoas(rs.getInt("numPessoas"));
                reserva.setOcasiao(TipoOcasiao.valueOf(rs.getString("ocasiao")));
                reserva.setStatusReserva(StatusReserva.valueOf(rs.getString("statusReserva")));
                reserva.setObservacao(rs.getString("observacao"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar reserva por ID: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
        return Optional.ofNullable(reserva);
    }

    @Override
    public List<Reserva> buscarTodos() throws SQLException {
        String sql = "SELECT id_reserva, id_cliente, id_mesa, dataHora, numPessoas, ocasiao, statusReserva, observacao " +
                "FROM Reserva WHERE statusReserva != 'CANCELADA'";
        System.out.println("ReservaDAO: Executando query: " + sql);
        List<Reserva> reservas = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setIdReserva(rs.getInt("id_reserva"));
                reserva.setIdCliente(rs.getInt("id_cliente"));
                reserva.setIdMesa(rs.getInt("id_mesa"));
                reserva.setDataHora(rs.getTimestamp("dataHora").toLocalDateTime());
                reserva.setNumPessoas(rs.getInt("numPessoas"));
                reserva.setOcasiao(TipoOcasiao.valueOf(rs.getString("ocasiao")));
                reserva.setStatusReserva(StatusReserva.valueOf(rs.getString("statusReserva")));
                reserva.setObservacao(rs.getString("observacao"));
                reservas.add(reserva);
                System.out.println("ReservaDAO: Reserva encontrada - ID: " + reserva.getIdReserva() + 
                    ", Cliente: " + reserva.getIdCliente() + 
                    ", Mesa: " + reserva.getIdMesa() + 
                    ", Data: " + reserva.getDataHora());
            }
            System.out.println("ReservaDAO: Total de reservas encontradas: " + reservas.size());
        } catch (SQLException e) {
            System.err.println("Erro detalhado ao buscar todas as reservas: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
        return reservas;
    }

    @Override
    public void atualizar(Reserva reserva) throws SQLException {
        String sql = "UPDATE Reserva SET id_cliente = ?, id_mesa = ?, dataHora = ?, numPessoas = ?, " +
                "ocasiao = ?, statusReserva = ?, observacao = ? WHERE id_reserva = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, reserva.getIdCliente());
            statement.setInt(2, reserva.getIdMesa());
            statement.setTimestamp(3, Timestamp.valueOf(reserva.getDataHora()));
            statement.setInt(4, reserva.getNumPessoas());
            statement.setString(5, reserva.getOcasiao().name());
            statement.setString(6, reserva.getStatusReserva().name());
            statement.setString(7, reserva.getObservacao());
            statement.setInt(8, reserva.getIdReserva());

            int linhasAfetadas = statement.executeUpdate();
            if (linhasAfetadas == 0) {
                throw new SQLException("Falha ao atualizar reserva, nenhuma reserva encontrada com o ID: " + reserva.getIdReserva());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar reserva: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
    }

    @Override
    public void deletar(int id) throws SQLException {
        String sql = "DELETE FROM Reserva WHERE id_reserva = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar reserva: " + e.getMessage());
            throw e;
        } finally {
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
    }

    @Override
    public List<Reserva> buscarPorData(LocalDate data) throws SQLException {
        String sql = "SELECT id_reserva, id_cliente, id_mesa, dataHora, numPessoas, ocasiao, statusReserva, observacao " +
                "FROM Reserva WHERE DATE(dataHora) = ?";
        List<Reserva> reservas = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(data));
            rs = statement.executeQuery();
            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setIdReserva(rs.getInt("id_reserva"));
                reserva.setIdCliente(rs.getInt("id_cliente"));
                reserva.setIdMesa(rs.getInt("id_mesa"));
                reserva.setDataHora(rs.getTimestamp("dataHora").toLocalDateTime());
                reserva.setNumPessoas(rs.getInt("numPessoas"));
                reserva.setOcasiao(TipoOcasiao.valueOf(rs.getString("ocasiao")));
                reserva.setStatusReserva(StatusReserva.valueOf(rs.getString("statusReserva")));
                reserva.setObservacao(rs.getString("observacao"));
                reservas.add(reserva);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar reservas por data: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
        return reservas;
    }

    @Override
    public List<Reserva> buscarPorCliente(int idCliente) throws SQLException {
        String sql = "SELECT id_reserva, id_cliente, id_mesa, dataHora, numPessoas, ocasiao, statusReserva, observacao " +
                "FROM Reserva WHERE id_cliente = ? AND statusReserva != 'CANCELADA'";
        List<Reserva> reservas = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idCliente);
            rs = statement.executeQuery();
            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setIdReserva(rs.getInt("id_reserva"));
                reserva.setIdCliente(rs.getInt("id_cliente"));
                reserva.setIdMesa(rs.getInt("id_mesa"));
                reserva.setDataHora(rs.getTimestamp("dataHora").toLocalDateTime());
                reserva.setNumPessoas(rs.getInt("numPessoas"));
                reserva.setOcasiao(TipoOcasiao.valueOf(rs.getString("ocasiao")));
                reserva.setStatusReserva(StatusReserva.valueOf(rs.getString("statusReserva")));
                reserva.setObservacao(rs.getString("observacao"));
                reservas.add(reserva);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar reservas por cliente: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
        return reservas;
    }

    @Override
    public List<Reserva> buscarPorMesaEPeriodo(int idMesa, LocalDateTime inicioPeriodoProposto, LocalDateTime fimPeriodoProposto) throws SQLException {
        return buscarPorMesaEPeriodo(idMesa, inicioPeriodoProposto, fimPeriodoProposto, null);
    }

    @Override
    public List<Reserva> buscarPorMesaEPeriodo(int idMesa, LocalDateTime inicioPeriodoProposto, LocalDateTime fimPeriodoProposto, Integer idReservaExcluir) throws SQLException {
        String duracaoReservaSQL = "02:00:00"; // Assumindo uma duração padrão de 2 horas para todas as reservas

        StringBuilder sql = new StringBuilder(
            "SELECT id_reserva, id_cliente, id_mesa, dataHora, numPessoas, ocasiao, statusReserva, observacao " +
            "FROM Reserva " +
            "WHERE id_mesa = ? " +
            "  AND statusReserva != 'CANCELADA' " +
            "  AND dataHora < ? " +
            "  AND ADDTIME(dataHora, ?) > ?"
        );

        if (idReservaExcluir != null) {
            sql.append(" AND id_reserva != ?");
        }

        List<Reserva> reservasConflitantes = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql.toString());

            int paramIndex = 1;
            statement.setInt(paramIndex++, idMesa);
            statement.setTimestamp(paramIndex++, Timestamp.valueOf(fimPeriodoProposto));
            statement.setString(paramIndex++, duracaoReservaSQL);
            statement.setTimestamp(paramIndex++, Timestamp.valueOf(inicioPeriodoProposto));
            
            if (idReservaExcluir != null) {
                statement.setInt(paramIndex, idReservaExcluir);
            }

            rs = statement.executeQuery();
            while (rs.next()) {
                Reserva reserva = new Reserva();
                reserva.setIdReserva(rs.getInt("id_reserva"));
                reserva.setIdCliente(rs.getInt("id_cliente"));
                reserva.setIdMesa(rs.getInt("id_mesa"));
                reserva.setDataHora(rs.getTimestamp("dataHora").toLocalDateTime());
                reserva.setNumPessoas(rs.getInt("numPessoas"));
                reserva.setOcasiao(TipoOcasiao.valueOf(rs.getString("ocasiao")));
                reserva.setStatusReserva(StatusReserva.valueOf(rs.getString("statusReserva")));
                reserva.setObservacao(rs.getString("observacao"));
                reservasConflitantes.add(reserva);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar reservas conflitantes por mesa e período: " + e.getMessage());
            throw e;
        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            releaseConnection(connection);
        }
        return reservasConflitantes;
    }
}