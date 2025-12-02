/* Reserva de Mesa */

package com.casacaribe.reservas.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Clase que modela la entidad ReservaMesa.
 * Sigue los principios de Encapsulamiento (atributos privados, getters/setters públicos).
 */
public class ReservaMesa {

    // Variables de instancia con nomenclatura camelCase y privadas
    private int id;
    private LocalDate fechaReserva;
    private LocalTime horaReserva;
    private int numPersonas;
    private String nombreCliente;
    private String emailCliente;
    private String estado;

    /**
     * Constructor vacío.
     */
    public ReservaMesa() {
    }

    /**
     * Constructor con todos los campos.
     * @param id El identificador único de la reserva.
     * @param fechaReserva La fecha de la reserva.
     * @param horaReserva La hora de la reserva.
     * @param numPersonas El número de personas.
     * @param nombreCliente El nombre del cliente.
     * @param emailCliente El correo electrónico del cliente.
     * @param estado El estado de la reserva (ej. Confirmada, Pendiente).
     */
    public ReservaMesa(int id, LocalDate fechaReserva, LocalTime horaReserva, int numPersonas, String nombreCliente, String emailCliente, String estado) {
        this.id = id;
        this.fechaReserva = fechaReserva;
        this.horaReserva = horaReserva;
        this.numPersonas = numPersonas;
        this.nombreCliente = nombreCliente;
        this.emailCliente = emailCliente;
        this.estado = estado;
    }

    // Nomenclatura de Métodos (getters y setters) en camelCase
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Sobreescribe el método toString para una representación legible del objeto.
     * @return Una cadena de texto con los datos de la reserva.
     */
    @Override
    public String toString() {
        return "ReservaMesa{" +
                "ID=" + id +
                ", Cliente='" + nombreCliente + '\'' +
                ", Fecha=" + fechaReserva +
                ", Hora=" + horaReserva +
                ", Personas=" + numPersonas +
                ", Estado='" + estado + '\'' +
                '}';
    }
}