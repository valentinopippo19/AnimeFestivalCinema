package patrones;

import ia.Recomendador;
import modelo.Pelicula;
import java.util.List;
import java.util.stream.Collectors;

/** Adapter: adapta una API externa que devuelve títulos al contrato Recomendador. */
public class RecomendadorAdapter implements Recomendador {
    private final RecomendadorExterno externo;
    public RecomendadorAdapter(RecomendadorExterno externo) { this.externo = externo; }

    @Override
    public List<Pelicula> recomendar(List<Pelicula> peliculas, int cantidad) {
        List<String> titulos = externo.obtenerTitulos("anime", cantidad);
        return peliculas.stream()
                .filter(p -> titulos.stream().anyMatch(t -> t.equalsIgnoreCase(p.getTitulo())))
                .collect(Collectors.toList());
    }
}
