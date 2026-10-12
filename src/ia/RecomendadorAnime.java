package ia;

import modelo.Pelicula;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Strategy Pattern: motor local de recomendación. Puede sustituirse por un modelo ML/API sin tocar la web. */
public class RecomendadorAnime implements Recomendador {
    @Override
    public List<Pelicula> recomendar(List<Pelicula> peliculas, int cantidad) {
        return peliculas.stream()
                .filter(Pelicula::isAnime)
                .sorted(Comparator.comparingDouble(Pelicula::calcularPromedio).reversed()
                        .thenComparing(Pelicula::getTitulo))
                .limit(cantidad)
                .collect(Collectors.toList());
    }
}
