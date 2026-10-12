package ia;

import modelo.Pelicula;
import java.util.List;

public interface Recomendador {
    List<Pelicula> recomendar(List<Pelicula> peliculas, int cantidad);
}
