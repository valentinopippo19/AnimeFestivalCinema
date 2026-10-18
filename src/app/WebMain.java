package app;

import excepciones.PersistenciaException;
import modelo.Festival;
import patrones.AnimeFestivalFacade;
import patrones.ReservaLogger;
import persistencia.PeliculaDAOMemoria;
import persistencia.PeliculaRepository;
import persistencia.PersistenciaFestivalCompleta;
import seguridad.CuentaRepositoryArchivo;
import seguridad.CuentaServicio;
import servicios.AnimeFestivalSeeder;
import servicios.EntradaServicio;
import servicios.FestivalServicio;
import servicios.PeliculaServicio;
import web.WebServer;

import java.awt.Desktop;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;

/** Punto de entrada de la interfaz web. Se conserva como segunda interfaz del sistema. */
public class WebMain {
    public static void main(String[] args) {
        try {
            Festival festival = new Festival("Anime Festival Cinema");
            FestivalServicio festivalServicio = new FestivalServicio(festival, new ArrayList<>());
            PeliculaRepository peliculaRepository = new PeliculaRepository(new PeliculaDAOMemoria());
            PeliculaServicio peliculaServicio = new PeliculaServicio(peliculaRepository);
            EntradaServicio entradaServicio = new EntradaServicio();
            entradaServicio.setFestival(festival);

            PersistenciaFestivalCompleta persistencia = new PersistenciaFestivalCompleta();
            boolean restaurado = false;
            if (persistencia.existe()) {
                try {
                    restaurado = persistencia.cargar(festivalServicio, peliculaServicio, entradaServicio);
                } catch (PersistenciaException e) {
                    System.err.println("No se pudo restaurar el estado: " + e.getMessage());
                }
            }
            if (!restaurado) new AnimeFestivalSeeder(festivalServicio, peliculaServicio, entradaServicio).cargar();

            entradaServicio.getEventos().suscribir(new ReservaLogger());
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    persistencia.guardar(festivalServicio, peliculaServicio, entradaServicio);
                } catch (PersistenciaException e) {
                    System.err.println("No se pudo guardar el estado: " + e.getMessage());
                }
            }, "anime-festival-persistencia-web"));

            AnimeFestivalFacade facade = new AnimeFestivalFacade(festivalServicio, peliculaServicio, entradaServicio);
            CuentaServicio cuentaServicio = new CuentaServicio(
                    new CuentaRepositoryArchivo("anime-festival-cuentas.data"), entradaServicio);
            Path webRoot = Path.of("src", "web").toAbsolutePath().normalize();
            WebServer server = new WebServer(peliculaServicio, entradaServicio, cuentaServicio,
                    festivalServicio, facade, webRoot);
            server.start(8765);

            String url = server.url();
            System.out.println("===============================================");
            System.out.println(" ANIME FESTIVAL CINEMA - PANEL WEB");
            System.out.println(" " + url);
            System.out.println("===============================================");
            try {
                if (!java.awt.GraphicsEnvironment.isHeadless() && Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(new URI(url));
                }
            } catch (Throwable e) {
                System.out.println("Abrí manualmente: " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
