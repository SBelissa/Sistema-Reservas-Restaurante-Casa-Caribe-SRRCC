/*JDB Proyecto: Casa Caribe - Sistema de Reservas
    * Archivo: ConexionDB.java
    * Descripción: Clase utilitaria para manejar la conexión JDBC a la base de datos.
    * Autor: Equipo de Desarrollo Casa Caribe
    * Fecha: 2024-06-15
    */

package com.casacaribe.reservas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase utilitaria para manejar la conexión JDBC a la base de datos del proyecto Casa Caribe.
 * Sigue el principio de Encapsulamiento al mantener los detalles de la conexión privados.
 */
public class ConexionDB {

    // Nomenclatura de Constantes en MAYÚSCULAS_CON_GUION_BAJO
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/casacaribe_db";
    private static final String JDBC_USER = "usuario_app";
    private static final String JDBC_PASSWORD = "tu_password_segura";

    /**
     * Establece y retorna una nueva conexión a la base de datos.
     *
     * @return Objeto Connection activo.
     * @throws SQLException Si ocurre un error de conexión a la base de datos.
     */
    public static Connection getConnection() throws SQLException {
        // Usa Log.i o System.out.println para propósitos de depuración
        System.out.println("Intentando conectar a la base de datos...");
        
        // Carga del driver JDBC (puede no ser necesario en Java 8+ si se usa el driver moderno)
        /* try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC no encontrado.");
            e.printStackTrace();
        }
        */
        
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }

    /**
     * Cierra la conexión JDBC de forma segura.
     *
     * @param connection La conexión a cerrar.
     */
    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión.");
            e.printStackTrace();
        }
    }
}