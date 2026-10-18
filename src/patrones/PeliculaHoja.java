package patrones;

import modelo.Pelicula;
import java.util.List;

/** Leaf del Composite. */
public class PeliculaHoja implements ElementoCartelera {
    private final Pelicula pelicula;
    public PeliculaHoja(Pelicula pelicula) { this.pelicula = pelicula; }
    @Override public String nombre() { return pelicula.getTitulo(); }
    @Override public List<Pelicula> peliculas() { return List.of(pelicula); }
}
