package patrones;

import modelo.*;
import servicios.EntradaServicio;
import servicios.FestivalServicio;
import servicios.PeliculaServicio;

import java.time.LocalDate;
import java.time.LocalTime;

/** Facade: ofrece operaciones de alto nivel sin exponer la coordinación entre servicios. */
public class AnimeFestivalFacade {
    private final FestivalServicio festival;
    private final PeliculaServicio peliculas;
    private final EntradaServicio entradas;

    public AnimeFestivalFacade(FestivalServicio festival, PeliculaServicio peliculas, EntradaServicio entradas) {
        this.festival = festival; this.peliculas = peliculas; this.entradas = entradas;
    }

    public void registrarPelicula(String titulo, String genero, int duracion, String director) {
        peliculas.registrarPelicula(titulo, genero, duracion, director);
    }

    public void programarPresencial(String titulo, String sala, LocalDate fecha, LocalTime hora) {
        entradas.programarFuncionPresencial(peliculas.buscarPelicula(titulo), festival.buscarSala(sala), fecha, hora);
    }

    public Entrada reservarPresencial(String espectador, String titulo, LocalDate fecha, int butaca) {
        Funcion f = entradas.buscarFuncion(titulo, fecha);
        if (!(f instanceof FuncionPresencial presencial))
            throw new IllegalArgumentException("La función no es presencial.");
        return entradas.venderEntradaPresencial(espectador, presencial, butaca);
    }

    public Festival getFestival() { return festival.getFestival(); }
    public CarteleraComposite cartelera(int anio) { return festival.construirCartelera(anio); }
}
