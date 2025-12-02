package com.casacaribe.reservas;

import com.casacaribe.reservas.modelo.ReservaMesa;
import com.casacaribe.reservas.repository.ReservaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Clase principal para demostrar las funcionalidades CRUD del módulo de reservas.
 * Esta clase simula el uso del servicio/repositorio por parte de la aplicación.
 */
public class MainDemo {

    public static void main(String[] args) {
        System.out.println("--- INICIANDO DEMOSTRACIÓN DEL MÓDULO DE RESERVAS CASA CARIBE (JDBC) ---");

        // Instancia del Repositorio (simula la capa de servicio)
        ReservaRepository reservaRepository = new ReservaRepository();

        // -----------------------------------------------------------
        // 1. CREAR RESERVA (C - Create)
        // -----------------------------------------------------------
        System.out.println("\n--- 1. Insertando Nueva Reserva ---");
        ReservaMesa nuevaReserva = new ReservaMesa();
        nuevaReserva.setFechaReserva(LocalDate.of(2025, 12, 10));
        nuevaReserva.setHoraReserva(LocalTime.of(19, 30));
        nuevaReserva.setNumPersonas(4);
        nuevaReserva.setNombreCliente("Sofía Gómez");
        nuevaReserva.setEmailCliente("sofia.gomez@ejemplo.com");
        // El estado se establece por defecto en la BD

        int nuevoId = reservaRepository.crearReserva(nuevaReserva);

        if (nuevoId != -1) {
            System.out.println("Reserva creada con éxito. ID: " + nuevoId);
            System.out.println("Detalles: " + nuevaReserva);
        } else {
            System.out.println("Fallo al crear la reserva. Revise logs y configuración de la DB.");
        }


        // -----------------------------------------------------------
        // 2. CONSULTAR RESERVA (R - Read)
        // -----------------------------------------------------------
        System.out.println("\n--- 2. Consultando Todas las Reservas ---");
        List<ReservaMesa> reservasActuales = reservaRepository.consultarTodas();
        reservasActuales.forEach(System.out::println);


        // -----------------------------------------------------------
        // 3. ACTUALIZAR RESERVA (U - Update)
        // -----------------------------------------------------------
        System.out.println("\n--- 3. Actualizando Reserva Creada ---");
        // Consultamos la reserva por ID antes de actualizarla
        Optional<ReservaMesa> reservaParaActualizarOpt = reservaRepository.consultarPorId(nuevoId);
        
        if (reservaParaActualizarOpt.isPresent()) {
            ReservaMesa reservaActualizar = reservaParaActualizarOpt.get();
            // Cambiamos el número de personas y el estado
            reservaActualizar.setNumPersonas(6);
            reservaActualizar.setEstado("Confirmada"); 
            
            boolean exitoActualizacion = reservaRepository.actualizarReserva(reservaActualizar);

            if (exitoActualizacion) {
                System.out.println("Reserva ID " + nuevoId + " actualizada. Nuevo estado: " + reservaActualizar.getEstado());
                // Volvemos a consultar para confirmar
                reservaRepository.consultarPorId(nuevoId).ifPresent(System.out::println);
            } else {
                System.out.println("Fallo al actualizar la reserva ID " + nuevoId);
            }
        } else {
            System.out.println("No se encontró la reserva para actualizar (ID: " + nuevoId + ")");
        }


        // -----------------------------------------------------------
        // 4. ELIMINAR RESERVA (D - Delete)
        // -----------------------------------------------------------
        System.out.println("\n--- 4. Eliminando Reserva ---");
        if (nuevoId != -1) {
            boolean exitoEliminacion = reservaRepository.eliminarReserva(nuevoId);

            if (exitoEliminacion) {
                System.out.println("Reserva ID " + nuevoId + " eliminada con éxito.");
            } else {
                System.out.println("Fallo al eliminar la reserva ID " + nuevoId + ". Es posible que ya no exista.");
            }
        }
        
        System.out.println("\n--- DEMOSTRACIÓN FINALIZADA ---");
    }
}