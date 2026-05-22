package com.b06_josueisraelvasquezmartinez.bloque4_axity.persistence;

import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DatabaseService {
    private final DataSource dataSource;

    // Spring inyecta automáticamente el DataSource configurado en
    // application.properties
    public DatabaseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Inserta de forma segura un registro de ingresos económicos en la base de
     * datos
     */
    public void appendRevenue(RevenueRecord record) {
        String sql = "INSERT INTO revenues (type, amount, tourist_id, zone, timestamp) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, record.type());
            ps.setDouble(2, record.amount());
            ps.setInt(3, record.touristId());
            ps.setString(4, record.zone());
            ps.setTimestamp(5, Timestamp.valueOf(record.timestamp()));

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println(" Error al persistir ingreso en DB: " + e.getMessage());
        }
    }

    /**
     * Inserta un registro de gastos operativos en la base de datos
     */
    public void appendExpense(ExpenseRecord record) {
        String sql = "INSERT INTO expenses (type, amount, description, timestamp) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, record.type());
            ps.setDouble(2, record.amount());
            ps.setString(3, record.description());
            ps.setTimestamp(4, Timestamp.valueOf(record.timestamp()));

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println(" Error al persistir gasto en DB: " + e.getMessage());
        }
    }

    /**
     * Guarda la bitácora de contingencias y alertas en la base de datos
     */
    public void appendEvent(EventRecord record) {
        String sql = "INSERT INTO events (step, event_name, description, affected_entities, timestamp) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, record.step());
            ps.setString(2, record.eventName());
            ps.setString(3, record.description());
            ps.setString(4, record.affectedEntities());
            ps.setTimestamp(5, Timestamp.valueOf(record.timestamp()));

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println(" Error al persistir evento aleatorio en DB: " + e.getMessage());
        }
    }

    /**
     * Extrae el histórico completo de ingresos económicos desde la DB
     */
    public List<RevenueRecord> getAllRevenues() {
        List<RevenueRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM revenues ORDER BY timestamp DESC";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new RevenueRecord(
                        rs.getLong("id"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        rs.getInt("tourist_id"),
                        rs.getString("zone"),
                        rs.getTimestamp("timestamp").toLocalDateTime()));
            }
        } catch (SQLException e) {
            System.err.println(" Error al leer ingresos de DB: " + e.getMessage());
        }
        return list;
    }

    /**
     * Extrae el registro de contingencias y alertas históricas
     */
    public List<EventRecord> getAllEvents() {
        List<EventRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM events ORDER BY step ASC";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new EventRecord(
                        rs.getLong("id"),
                        rs.getLong("step"),
                        rs.getString("event_name"),
                        rs.getString("description"),
                        rs.getString("affected_entities"),
                        rs.getTimestamp("timestamp").toLocalDateTime()));
            }
        } catch (SQLException e) {
            System.err.println(" Error al leer eventos de DB: " + e.getMessage());
        }
        return list;
    }
}
