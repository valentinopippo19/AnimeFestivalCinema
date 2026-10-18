package persistencia;

import excepciones.PersistenciaException;
import modelo.*;
import servicios.EntradaServicio;
import servicios.FestivalServicio;
import servicios.PeliculaServicio;

import java.io.*;

/**
 * Persistencia completa del dominio mediante un snapshot binario versionado.
 * La interfaz web no cambia: el snapshot solo restaura el estado al arrancar y lo guarda al salir.
 */
public class PersistenciaFestivalCompleta {
    private static final String ARCHIVO = "anime-festival.data";

    public boolean existe() { return new File(ARCHIVO).isFile(); }

    public void guardar(FestivalServicio festivalServicio, PeliculaServicio peliculaServicio,
                        EntradaServicio entradaServicio) throws PersistenciaException {
        FestivalSnapshot snapshot = new FestivalSnapshot(
                festivalServicio.getFestival(), festivalServicio.getSalas(),
                peliculaServicio.getDirectores(), peliculaServicio.getActores(), peliculaServicio.getJurados(),
                entradaServicio.getEspectadores(), peliculaServicio.getPeliculas(), entradaServicio.getFunciones(),
                entradaServicio.getEntradas(), entradaServicio.getAbonos(), peliculaServicio.getEvaluaciones(),
                peliculaServicio.getVotosPublico(), entradaServicio.getContadorCodigo());
        File destino = new File(ARCHIVO);
        File temporal = new File(ARCHIVO + ".tmp");
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(temporal)))) {
            out.writeObject(snapshot);
        } catch (IOException e) {
            temporal.delete();
            throw new PersistenciaException("No se pudo guardar el estado completo del festival: " + e.getMessage());
        }
        if (!temporal.renameTo(destino)) {
            try {
                java.nio.file.Files.move(temporal.toPath(), destino.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING,
                        java.nio.file.StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                throw new PersistenciaException("No se pudo reemplazar el archivo de persistencia.");
            }
        }
    }

    public boolean cargar(FestivalServicio festivalServicio, PeliculaServicio peliculaServicio,
                          EntradaServicio entradaServicio) throws PersistenciaException {
        if (!existe()) return false;
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(ARCHIVO)))) {
            Object objeto = in.readObject();
            if (!(objeto instanceof FestivalSnapshot snapshot)) {
                throw new PersistenciaException("El archivo de persistencia no contiene un snapshot válido.");
            }
            festivalServicio.restaurar(snapshot.festival, snapshot.salas);
            peliculaServicio.restaurar(snapshot.peliculas, snapshot.directores, snapshot.actores,
                    snapshot.jurados, snapshot.evaluaciones, snapshot.votosPublico);
            entradaServicio.restaurar(snapshot.espectadores, snapshot.funciones, snapshot.entradas,
                    snapshot.abonos, snapshot.contadorCodigo);
            entradaServicio.setFestival(snapshot.festival);
            return true;
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenciaException("No se pudo cargar el estado completo del festival: " + e.getMessage());
        }
    }
}
