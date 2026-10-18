package persistencia;

import excepciones.PersistenciaException;
import modelo.Pelicula;
import java.util.List;
import java.util.Optional;

/** DAO: contrato CRUD orientado a la entidad Pelicula. */
public interface PeliculaDAO {
    void guardar(Pelicula pelicula) throws PersistenciaException;
    Optional<Pelicula> buscarPorTitulo(String titulo) throws PersistenciaException;
    List<Pelicula> listar() throws PersistenciaException;
}
