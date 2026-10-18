package modelo;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

public class Pelicula implements Serializable {
    private String titulo;
    private String genero;
    private int duracion;
    private Director director;
    private List<Actor> actores;
    private List<Evaluacion> evaluaciones;
    private List<Funcion> funciones;
    private List<VotoPublico> votosPublico;
    private boolean anime;
    private String imagenUrl;
    private String descripcion;

    public Pelicula(String titulo, String genero, int duracion, Director director) {
        this(titulo, genero, duracion, director, false, "", "");
    }

    public Pelicula(String titulo, String genero, int duracion, Director director, boolean anime, String imagenUrl) {
        this(titulo, genero, duracion, director, anime, imagenUrl, "");
    }

    public Pelicula(String titulo, String genero, int duracion, Director director, boolean anime, String imagenUrl, String descripcion) {
        if (titulo == null || titulo.isBlank()) throw new excepciones.FestivalException("El título es obligatorio.");
        if (genero == null || genero.isBlank()) throw new excepciones.FestivalException("El género es obligatorio.");
        if (duracion <= 0) throw new excepciones.FestivalException("La duración debe ser mayor a 0 minutos.");
        if (director == null) throw new excepciones.FestivalException("La película debe tener un director.");
        this.titulo = titulo.trim();
        this.genero = genero.trim();
        this.duracion = duracion;
        this.director = director;
        this.anime = anime;
        this.imagenUrl = imagenUrl == null ? "" : imagenUrl;
        this.descripcion = descripcion == null ? "" : descripcion;
        this.actores = new ArrayList<>();
        this.evaluaciones = new ArrayList<>();
        this.funciones = new ArrayList<>();
        this.votosPublico = new ArrayList<>();
    }

    public void agregarActor(Actor a) { if (a != null && !actores.contains(a)) actores.add(a); }
    public void agregarEvaluacion(Evaluacion e) {
        if (e == null) return;
        if (fueEvaluadaPor(e.getJurado())) {
            throw new excepciones.FestivalException("El jurado ya evaluó esta película.");
        }
        evaluaciones.add(e);
    }

    public boolean fueEvaluadaPor(Jurado jurado) {
        for (Evaluacion e : evaluaciones) if (e.getJurado() == jurado) return true;
        return false;
    }
    public void agregarFuncion(Funcion f) { funciones.add(f); }
    public void agregarVotoPublico(VotoPublico v) { votosPublico.add(v); }
    public boolean fueVotadaPor(Espectador espectador) {
        for (VotoPublico v : votosPublico) if (v.getEspectador() == espectador) return true;
        return false;
    }
    public double calcularPromedioPublico() {
        if (votosPublico.isEmpty()) return 0;
        double suma = 0;
        for (VotoPublico v : votosPublico) suma += v.getPuntaje();
        return suma / votosPublico.size();
    }

    public double calcularPromedio() {
        if (evaluaciones.isEmpty()) return 0;
        double suma = 0;
        for (Evaluacion e : evaluaciones) suma += e.getPuntaje();
        return suma / evaluaciones.size();
    }

    public String getTitulo() { return titulo; }
    public String getGenero() { return genero; }
    public int getDuracion() { return duracion; }
    public Director getDirector() { return director; }
    public List<Actor> getActores() { return actores; }
    public List<Evaluacion> getEvaluaciones() { return evaluaciones; }
    public List<VotoPublico> getVotosPublico() { return votosPublico; }
    public List<Funcion> getFunciones() { return funciones; }
    public boolean isAnime() { return anime; }
    public String getImagenUrl() { return imagenUrl; }
    public String getDescripcion() { return descripcion; }
    public void setAnime(boolean anime) { this.anime = anime; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl == null ? "" : imagenUrl; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion == null ? "" : descripcion; }

    @Override
    public String toString() {
        return titulo + " (" + genero + ", " + duracion + " min) - Dir: " + director.getNombre();
    }
}
