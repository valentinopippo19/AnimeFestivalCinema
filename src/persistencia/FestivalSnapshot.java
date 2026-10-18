package persistencia;

import modelo.*;
import java.io.Serializable;
import java.util.List;

/** Snapshot completo del dominio. Mantiene la aplicación web y evita perder datos al reiniciar. */
public class FestivalSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;
    public Festival festival;
    public List<Sala> salas;
    public List<Director> directores;
    public List<Actor> actores;
    public List<Jurado> jurados;
    public List<Espectador> espectadores;
    public List<Pelicula> peliculas;
    public List<Funcion> funciones;
    public List<Entrada> entradas;
    public List<Abono> abonos;
    public List<Evaluacion> evaluaciones;
    public List<VotoPublico> votosPublico;
    public int contadorCodigo;

    public FestivalSnapshot(Festival festival, List<Sala> salas, List<Director> directores,
                            List<Actor> actores, List<Jurado> jurados, List<Espectador> espectadores,
                            List<Pelicula> peliculas, List<Funcion> funciones, List<Entrada> entradas,
                            List<Abono> abonos, List<Evaluacion> evaluaciones, List<VotoPublico> votosPublico,
                            int contadorCodigo) {
        this.festival = festival; this.salas = salas; this.directores = directores; this.actores = actores;
        this.jurados = jurados; this.espectadores = espectadores; this.peliculas = peliculas;
        this.funciones = funciones; this.entradas = entradas; this.abonos = abonos;
        this.evaluaciones = evaluaciones; this.votosPublico = votosPublico; this.contadorCodigo = contadorCodigo;
    }
}
