package persistencia;

import excepciones.PersistenciaException;
import modelo.Pelicula;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** DAO alternativo para pruebas: misma abstracción, sin depender del almacenamiento físico. */
public class PeliculaDAOMemoria implements PeliculaDAO {
    private final Map<String, Pelicula> peliculas = new ConcurrentHashMap<>();
    @Override public void guardar(Pelicula pelicula) { peliculas.put(pelicula.getTitulo().toLowerCase(), pelicula); }
    @Override public Optional<Pelicula> buscarPorTitulo(String titulo) { return Optional.ofNullable(peliculas.get(titulo.toLowerCase())); }
    @Override public List<Pelicula> listar() { return new ArrayList<>(peliculas.values()); }
}
