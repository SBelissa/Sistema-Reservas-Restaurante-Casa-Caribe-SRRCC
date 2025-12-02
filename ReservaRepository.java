package com.casacaribe.reservas.modelo;

import com.casacaribe.reservas.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Clase Repository (DAO - Data Access Object) para gestionar la persistencia
 * de la entidad ReservaMesa utilizando JDBC puro.
 * Nomenclatura de clase en CamelCase, métodos en camelCase.
 */
public class ReservaRepository {
    // Nomenclatura de Constantes para las consultas SQL
    private static final String SQL_INSERT = "INSERT INTO reservas (fecha_reserva, hora_reserva, num_personas, nombre_cliente, email_cliente) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_SELECT_ALL = "SELECT * FROM reservas ORDER BY fecha_reserva, hora_reserva";
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM reservas WHERE id = ?";
    private static final String SQL_UPDATE = "UPDATE reservas SET fecha_reserva = ?, hora_reserva = ?, num_personas = ?, estado = ? WHERE id = ?";
    private static final String SQL_DELETE = "DELETE FROM reservas WHERE id = ?";

    /**
     * Inserta una nueva reserva en la base de datos (Operación C - Create).
     *
     * @param reserva Objeto ReservaMesa a persistir.
     * @return El ID generado de la nueva reserva, o -1 si falla.
     */
    public int crearReserva(ReservaMesa reserva) {
        int idGenerado = -1;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            // Retorna las claves generadas
            ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            
            // Seteo de parámetros
            ps.setDate(1, Date.valueOf(reserva.getFechaReserva()));
            ps.setTime(2, Time.valueOf(reserva.getHoraReserva()));
            ps.setInt(3, reserva.getNumPersonas());
            ps.setString(4, reserva.getNombreCliente());
            ps.setString(5, reserva.getEmailCliente());
            
            // Ejecución de la consulta
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                // Obtener el ID generado
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    idGenerado = rs.getInt(1);
                    reserva.setId(idGenerado); // Actualiza el objeto con el ID
                }
            }
        } catch (SQLException e) {
            // Manejo de Excepción: Imprimir el error
            System.err.println("ERROR en crearReserva: " + e.getMessage());
        } finally {
            // Cierre seguro de recursos
            try { if (rs != null) rs.close(); } catch (SQLException e) {/* ignore */}
            try { if (ps != null) ps.close(); } catch (SQLException e) {/* ignore */}
            ConexionDB.closeConnection(conn);
        }
        return idGenerado;
    }

    /**
     * Consulta todas las reservas en la base de datos (Operación R - Read).
     *
     * @return Lista de objetos ReservaMesa.
     */
    public List<ReservaMesa> consultarTodas() {
        List<ReservaMesa> reservas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_ALL);
            rs = ps.executeQuery(); // Ejecución de la consulta SELECT

            while (rs.next()) {
                // Mapeo del ResultSet al objeto ReservaMesa
                ReservaMesa reserva = new ReservaMesa(
                    rs.getInt("id"),
                    rs.getDate("fecha_reserva").toLocalDate(),
                    rs.getTime("hora_reserva").toLocalTime(),
                    rs.getInt("num_personas"),
                    rs.getString("nombre_cliente"),
                    rs.getString("email_cliente"),
                    rs.getString("estado")
                );
                reservas.add(reserva);
            }
        } catch (SQLException e) {
            System.err.println("ERROR en consultarTodas: " + e.getMessage());
        } finally {
            // Cierre seguro de recursos
            try { if (rs != null) rs.close(); } catch (SQLException e) {/* ignore */}
            try { if (ps != null) ps.close(); } catch (SQLException e) {/* ignore */}
            ConexionDB.closeConnection(conn);
        }
        return reservas;
    }

    /**
     * Busca una reserva por su ID (Operación R - Read).
     *
     * @param id El ID de la reserva a buscar.
     * @return Un Optional que contiene la ReservaMesa si se encuentra, o vacío si no.
     */
    public Optional<ReservaMesa> consultarPorId(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(SQL_SELECT_BY_ID);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                ReservaMesa reserva = new ReservaMesa(
                    rs.getInt("id"),
                    rs.getDate("fecha_reserva").toLocalDate(),
                    rs.getTime("hora_reserva").toLocalTime(),
                    rs.getInt("num_personas"),
                    rs.getString("nombre_cliente"),
                    rs.getString("email_cliente"),
                    rs.getString("estado")
                );
                return Optional.of(reserva);
            }
        } catch (SQLException e) {
            System.err.println("ERROR en consultarPorId: " + e.getMessage());
        } finally {
            // Cierre seguro de recursos
            try { if (rs != null) rs.close(); } catch (SQLException e) {/* ignore */}
            try { if (ps != null) ps.close(); } catch (SQLException e) {/* ignore */}
            ConexionDB.closeConnection(conn);
        }
        return Optional.empty(); // Retorna un Optional vacío si no se encuentra
    }
    
    /**
     * Actualiza una reserva existente (Operación U - Update).
     *
     * @param reserva Objeto ReservaMesa con los datos actualizados.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public boolean actualizarReserva(ReservaMesa reserva) {
        Connection conn = null;
        PreparedStatement ps = null;
        int filasAfectadas = 0;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(SQL_UPDATE);
            
            // Seteo de parámetros para la actualización
            ps.setDate(1, Date.valueOf(reserva.getFechaReserva()));
            ps.setTime(2, Time.valueOf(reserva.getHoraReserva()));
            ps.setInt(3, reserva.getNumPersonas());
            ps.setString(4, reserva.getEstado());
            ps.setInt(5, reserva.getId()); // ID para la cláusula WHERE

            filasAfectadas = ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("ERROR en actualizarReserva: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {/* ignore */}
            ConexionDB.closeConnection(conn);
        }
        return filasAfectadas > 0;
    }

    /**
     * Elimina una reserva por su ID (Operación D - Delete).
     *
     * @param id El ID de la reserva a eliminar.
     * @return true si la eliminación fue exitosa, false en caso contrario.
     */
    public boolean eliminarReserva(int id) {
        Connection conn = null;
        PreparedStatement ps = null;
        int filasAfectadas = 0;

        try {
            conn = ConexionDB.getConnection();
            ps = conn.prepareStatement(SQL_DELETE);
            ps.setInt(1, id);

            filasAfectadas = ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("ERROR en eliminarReserva: " + e.getMessage());
        } finally {
            try { if (ps != null) ps.close(); } catch (SQLException e) {/* ignore */}
            ConexionDB.closeConnection(conn);
        }
        return filasAfectadas > 0;
    }
}