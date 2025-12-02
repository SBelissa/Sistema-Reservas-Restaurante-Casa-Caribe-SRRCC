package com.casacaribe.reservas.modelo;

// Importaciones necesarias para manejar tipos de datos
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Clase Modelo (Java Bean) para representar una Reserva del restaurante.
 * Contiene los atributos de la entidad, con getters y setters.
 */
public class Reserva {
    private int id;
    private String nombreCliente;
    private String emailCliente;
    private LocalDate fechaReserva;
    private LocalTime horaReserva;
    private int numPersonas;
    private String estado; // Pendiente, Confirmada, Cancelada

    // Contador estático para simular IDs autoincrementables
    private static int nextId = 1;

    // Constructor vacío (necesario para Servlets/JSP)
    public Reserva() {
    }

    // Constructor para crear nuevas reservas
    public Reserva(String nombreCliente, String emailCliente, LocalDate fechaReserva, LocalTime horaReserva, int numPersonas, String estado) {
        this.id = nextId++;
        this.nombreCliente = nombreCliente;
        this.emailCliente = emailCliente;
        this.fechaReserva = fechaReserva;
        this.horaReserva = horaReserva;
        this.numPersonas = numPersonas;
        this.estado = estado;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public LocalDate getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDate fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalTime getHoraReserva() {
        return horaReserva;
    }

    public void setHoraReserva(LocalTime horaReserva) {
        this.horaReserva = horaReserva;
    }

    public int getNumPersonas() {
        return numPersonas;
    }

    public void setNumPersonas(int numPersonas) {
        this.numPersonas = numPersonas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}