package app;

import modelo.Festival;
import seguridad.CuentaRepositoryMemoria;
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

/** Punto de entrada. El sistema ya no abre Swing: levanta el panel web y abre el navegador. */
public class Main {
    public static void main(String[] args) throws Exception {
        Festival festival = new Festival("Anime Festival Cinema");
        FestivalServicio festivalServicio = new FestivalServicio(festival, new ArrayList<>());
        PeliculaServicio peliculaServicio = new PeliculaServicio();
        EntradaServicio entradaServicio = new EntradaServicio();

        new AnimeFestivalSeeder(festivalServicio, peliculaServicio, entradaServicio).cargar();

        CuentaServicio cuentaServicio = new CuentaServicio(new CuentaRepositoryMemoria(), entradaServicio);
        Path webRoot = Path.of("src", "web").toAbsolutePath().normalize();
        WebServer server = new WebServer(peliculaServicio, entradaServicio, cuentaServicio, festivalServicio, webRoot);
        server.start(8765);

        String url = server.url();
        System.out.println("===============================================");
        System.out.println(" ANIME FESTIVAL CINEMA - SERVIDOR WEB");
        System.out.println(" " + url);
        System.out.println("===============================================");
        try {
            if (!java.awt.GraphicsEnvironment.isHeadless() && Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI(url));
        } catch (Throwable e) {
            System.out.println("Abrí manualmente: " + url);
        }
    }
}
