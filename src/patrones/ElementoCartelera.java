package patrones;

import modelo.Pelicula;
import java.util.List;

/** Composite: contrato común para hojas y composiciones de la cartelera. */
public interface ElementoCartelera {
    String nombre();
    List<Pelicula> peliculas();
    default int cantidadPeliculas() { return peliculas().size(); }
}
