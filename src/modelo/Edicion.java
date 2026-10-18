package modelo;

import java.io.Serializable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Edicion implements Serializable {
    private int anio;
    private String ciudad;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private List<Seccion> secciones;

    public Edicion(int anio, String ciudad, LocalDate fechaInicio, LocalDate fechaFin) {
        if (anio <= 2000 || anio > 2100) throw new excepciones.FestivalException("El año de la edición no es válido.");
        if (ciudad == null || ciudad.isBlank()) throw new excepciones.FestivalException("La ciudad es obligatoria.");
        if (fechaInicio == null || fechaFin == null || fechaFin.isBefore(fechaInicio)) throw new excepciones.FestivalException("El rango de fechas de la edición no es válido.");
        this.anio = anio;
        this.ciudad = ciudad.trim();
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.secciones = new ArrayList<>();
    }

    public boolean contieneFecha(java.time.LocalDate fecha) {
        return fecha != null && !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public boolean incluye(Pelicula pelicula) {
        if (pelicula == null) return false;
        for (Seccion s : secciones) if (s.getPeliculas().contains(pelicula)) return true;
        return false;
    }

    public void agregarSeccion(Seccion s) {
        secciones.add(s);
    }

    public Seccion buscarSeccion(TipoSeccion tipo) {
        for (Seccion s : secciones) {
            if (s.getTipo() == tipo) return s;
        }
        return null;
    }

    public Pelicula determinarGanadoraPublico() {
        Pelicula ganadora = null;
        for (Seccion s : secciones) for (Pelicula p : s.getPeliculas()) {
            if (p.getVotosPublico().isEmpty()) continue;
            if (ganadora == null || p.calcularPromedioPublico() > ganadora.calcularPromedioPublico()) ganadora = p;
        }
        return ganadora;
    }

    public int getAnio() {
        return anio;
    }
    public String getCiudad() {
        return ciudad;
    }
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    public LocalDate getFechaFin() {
        return fechaFin;
    }
    public List<Seccion> getSecciones() {
        return secciones;
    }

    @Override
    public String toString() {
        return "Edicion " + anio + " - " + ciudad;
    }
}