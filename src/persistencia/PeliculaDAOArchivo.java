package persistencia;

import excepciones.PersistenciaException;
import modelo.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** DAO concreto que utiliza Table Data Gateway. */
public class PeliculaDAOArchivo implements PeliculaDAO {
    private final PeliculaTableDataGateway gateway;
    private final List<Director> directores;

    public PeliculaDAOArchivo(PeliculaTableDataGateway gateway, List<Director> directores) {
        this.gateway = gateway; this.directores = directores;
    }

    @Override
    public void guardar(Pelicula pelicula) throws PersistenciaException { gateway.insertar(pelicula); }

    @Override
    public Optional<Pelicula> buscarPorTitulo(String titulo) throws PersistenciaException {
        return listar().stream().filter(p -> p.getTitulo().equalsIgnoreCase(titulo)).findFirst();
    }

    @Override
    public List<Pelicula> listar() throws PersistenciaException {
        List<Pelicula> resultado = new ArrayList<>();
        for (String[] f : gateway.seleccionarTodas()) {
            if (f.length < 4) continue;
            for (Director d : directores) if (d.getNombre().equalsIgnoreCase(f[3])) {
                resultado.add(new Pelicula(f[0], f[1], Integer.parseInt(f[2]), d)); break;
            }
        }
        return resultado;
    }
}
