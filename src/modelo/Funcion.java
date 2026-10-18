package modelo;

import java.io.Serializable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import patrones.EstadoFuncion;
import patrones.FuncionDisponible;
import patrones.FuncionAgotada;
import patrones.FuncionFinalizada;

public abstract class Funcion implements Serializable {
    private LocalDate fecha;
    private LocalTime horario;
    private Pelicula pelicula;
    private String ciudad;
    private String pais;
    private EstadoFuncion estado = new FuncionDisponible();

    public Funcion(LocalDate fecha, LocalTime horario, Pelicula pelicula) {
        this(fecha, horario, pelicula, "Festival Online", "Mundo");
    }

    public Funcion(LocalDate fecha, LocalTime horario, Pelicula pelicula, String ciudad, String pais) {
        if (fecha == null || horario == null) throw new excepciones.FestivalException("La fecha y el horario son obligatorios.");
        if (pelicula == null) throw new excepciones.FestivalException("La función debe tener una película.");
        if (ciudad == null || ciudad.isBlank() || pais == null || pais.isBlank()) throw new excepciones.FestivalException("La ciudad y el país son obligatorios.");
        this.fecha = fecha;
        this.horario = horario;
        this.pelicula = pelicula;
        this.ciudad = ciudad.trim();
        this.pais = pais.trim();
    }

    public abstract boolean verificarDisponibilidad();
    public abstract double calcularOcupacion();

    /** State: actualiza el estado de la función a partir de tiempo y capacidad. */
    public void actualizarEstado() {
        if (yaFinalizo()) {
            estado = new FuncionFinalizada();
        } else if (!verificarDisponibilidad()) {
            estado = new FuncionAgotada();
        } else {
            estado = new FuncionDisponible();
        }
    }

    public boolean puedeReservar() {
        actualizarEstado();
        return estado.puedeReservar(this);
    }

    public String getEstado() {
        actualizarEstado();
        return estado.nombre();
    }

    public LocalDateTime getFechaHoraInicio() {
        return LocalDateTime.of(fecha, horario);
    }

    public LocalDateTime getFechaHoraFin() {
        return getFechaHoraInicio().plusMinutes(pelicula.getDuracion());
    }

    public boolean yaFinalizo() {
        return !getFechaHoraFin().isAfter(LocalDateTime.now());
    }

    /** Dos funciones se superponen si sus intervalos [inicio, fin) se intersectan. */
    public boolean seSuperponeCon(Funcion otra) {
        if (otra == null || !getFecha().equals(otra.getFecha())) return false;
        return getFechaHoraInicio().isBefore(otra.getFechaHoraFin())
                && otra.getFechaHoraInicio().isBefore(getFechaHoraFin());
    }

    public LocalDate getFecha() { return fecha; }
    public LocalTime getHorario() { return horario; }
    public Pelicula getPelicula() { return pelicula; }
    public String getCiudad() { return ciudad; }
    public String getPais() { return pais; }

    @Override
    public String toString() {
        return pelicula.getTitulo() + " - " + fecha + " " + horario + " - " + ciudad + ", " + pais;
    }
}
