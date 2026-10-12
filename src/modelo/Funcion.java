package modelo;

import java.time.LocalDate;
import java.time.LocalTime;

public abstract class Funcion {
    private LocalDate fecha;
    private LocalTime horario;
    private Pelicula pelicula;
    private String ciudad;
    private String pais;

    public Funcion(LocalDate fecha, LocalTime horario, Pelicula pelicula) {
        this(fecha, horario, pelicula, "Festival Online", "Mundo");
    }

    public Funcion(LocalDate fecha, LocalTime horario, Pelicula pelicula, String ciudad, String pais) {
        this.fecha = fecha;
        this.horario = horario;
        this.pelicula = pelicula;
        this.ciudad = ciudad;
        this.pais = pais;
    }

    public abstract boolean verificarDisponibilidad();
    public abstract double calcularOcupacion();

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
