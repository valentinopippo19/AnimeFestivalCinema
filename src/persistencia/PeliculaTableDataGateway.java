package persistencia;

import excepciones.PersistenciaException;
import modelo.Pelicula;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** Table Data Gateway: conoce el formato físico de la tabla/archivo de películas. */
public class PeliculaTableDataGateway {
    private final File archivo;

    public PeliculaTableDataGateway(String ruta) { this.archivo = new File(ruta); }

    public void insertar(Pelicula pelicula) throws PersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            bw.write(pelicula.getTitulo() + "," + pelicula.getGenero() + "," +
                    pelicula.getDuracion() + "," + pelicula.getDirector().getNombre());
            bw.newLine();
        } catch (IOException e) {
            throw new PersistenciaException("Error al insertar película en el gateway.");
        }
    }

    public List<String[]> seleccionarTodas() throws PersistenciaException {
        List<String[]> filas = new ArrayList<>();
        if (!archivo.exists()) return filas;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) filas.add(linea.split(",", -1));
            return filas;
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer la tabla de películas.");
        }
    }
}
