package persistencia;

import excepciones.PersistenciaException;
import modelo.Pelicula;
import java.util.List;
import java.util.Optional;

/** Repository: abstrae el acceso al conjunto de películas y desacopla el servicio del DAO. */
public class PeliculaRepository {
    private final PeliculaDAO dao;
    public PeliculaRepository(PeliculaDAO dao) { this.dao = dao; }
    public void guardar(Pelicula pelicula) throws PersistenciaException { dao.guardar(pelicula); }
    public Optional<Pelicula> buscarPorTitulo(String titulo) throws PersistenciaException { return dao.buscarPorTitulo(titulo); }
    public List<Pelicula> listar() throws PersistenciaException { return dao.listar(); }
}
