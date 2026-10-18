package app;

import excepciones.PersistenciaException;
import modelo.Festival;
import persistencia.Persistencia;
import persistencia.PersistenciaFestivalCompleta;
import persistencia.PeliculaDAOMemoria;
import persistencia.PeliculaRepository;
import seguridad.CuentaRepositoryArchivo;
import seguridad.CuentaServicio;
import servicios.AnimeFestivalSeeder;
import servicios.EntradaServicio;
import servicios.FestivalServicio;
import servicios.PeliculaServicio;
import ui.VentanaPrincipal;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Punto de entrada oficial de la aplicación de escritorio requerida por el TP.
 * La interfaz web se conserva y puede iniciarse con --web.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && "--web".equalsIgnoreCase(args[0])) {
            WebMain.main(new String[0]);
            return;
        }
        iniciarSwing();
    }

    private static void iniciarSwing() {
        SwingUtilities.invokeLater(() -> {
            try {
                Festival festival = new Festival("Anime Festival Cinema");
                FestivalServicio festivalServicio = new FestivalServicio(festival, new ArrayList<>());
                PeliculaRepository peliculaRepository = new PeliculaRepository(new PeliculaDAOMemoria());
                PeliculaServicio peliculaServicio = new PeliculaServicio(peliculaRepository);
                EntradaServicio entradaServicio = new EntradaServicio();
                entradaServicio.setFestival(festival);

                PersistenciaFestivalCompleta snapshot = new PersistenciaFestivalCompleta();
                boolean restaurado = false;
                if (snapshot.existe()) {
                    try {
                        restaurado = snapshot.cargar(festivalServicio, peliculaServicio, entradaServicio);
                    } catch (PersistenciaException e) {
                        JOptionPane.showMessageDialog(null,
                                "No se pudo cargar el estado guardado. Se iniciarán los datos de demostración.\n" + e.getMessage(),
                                "Advertencia de persistencia", JOptionPane.WARNING_MESSAGE);
                    }
                }
                if (!restaurado) {
                    new AnimeFestivalSeeder(festivalServicio, peliculaServicio, entradaServicio).cargar();
                }

                // Se mantiene el repositorio de cuentas para no perder la funcionalidad existente.
                new CuentaServicio(new CuentaRepositoryArchivo("anime-festival-cuentas.data"), entradaServicio);

                VentanaPrincipal ventana = new VentanaPrincipal(
                        festivalServicio,
                        peliculaServicio,
                        entradaServicio,
                        new Persistencia());
                ventana.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "No se pudo iniciar Anime Festival Cinema:\n" + e.getMessage(),
                        "Error de inicio", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
