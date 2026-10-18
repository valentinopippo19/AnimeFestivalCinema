package patrones;

import modelo.Pelicula;
import java.util.ArrayList;
import java.util.List;

/** Composite: agrupa películas y otros elementos de cartelera. */
public class CarteleraComposite implements ElementoCartelera {
    private final String nombre;
    private final List<ElementoCartelera> elementos = new ArrayList<>();

    public CarteleraComposite(String nombre) { this.nombre = nombre; }
    public void agregar(ElementoCartelera elemento) { elementos.add(elemento); }
    public void quitar(ElementoCartelera elemento) { elementos.remove(elemento); }
    @Override public String nombre() { return nombre; }
    @Override public List<Pelicula> peliculas() {
        List<Pelicula> resultado = new ArrayList<>();
        for (ElementoCartelera e : elementos) resultado.addAll(e.peliculas());
        return resultado;
    }
}
