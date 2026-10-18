import excepciones.FestivalException;
import modelo.*;
import servicios.EntradaServicio;
import servicios.FestivalServicio;
import servicios.PeliculaServicio;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Suite de pruebas ejecutable sin dependencias externas.
 * Compilar con: javac -cp build -d build tests/ReglasNegocioTest.java
 * Ejecutar con: java -cp build ReglasNegocioTest
 */
public class ReglasNegocioTest {
    private static int ejecutadas;

    public static void main(String[] args) {
        testEmailInvalido();
        testEvaluacionDuplicada();
        testSuperposicionPorDuracion();
        testFechaFueraDeEdicion();
        testVentaDeFuncionFinalizada();
        testButacaDuplicada();
        System.out.println("OK - " + ejecutadas + " pruebas de reglas de negocio.");
    }

    private static void testEmailInvalido() {
        assertThrows(() -> new Director("A", "correo-invalido", "Argentina"), "email inválido");
    }

    private static void testEvaluacionDuplicada() {
        PeliculaServicio ps = new PeliculaServicio();
        ps.registrarDirector("Director", "director@test.com", "Argentina");
        ps.registrarJurado("Jurado", "jurado@test.com", "Argentina");
        ps.registrarPelicula("Anime", "Acción", 100, "Director");
        ps.registrarEvaluacion("Jurado", "Anime", 9);
        assertThrows(() -> ps.registrarEvaluacion("Jurado", "Anime", 8), "evaluación duplicada");
    }

    private static void testSuperposicionPorDuracion() {
        Festival festival = new Festival("Anime Festival Cinema");
        FestivalServicio fs = new FestivalServicio(festival, new ArrayList<>());
        LocalDate inicio = LocalDate.now().plusDays(2);
        fs.registrarEdicion(2026, "Buenos Aires", inicio, inicio.plusDays(5));
        fs.registrarSeccion(2026, TipoSeccion.COMPETENCIA_OFICIAL, "Premio", "Test");
        PeliculaServicio ps = new PeliculaServicio();
        ps.registrarDirector("Director", "director2@test.com", "Argentina");
        ps.registrarPelicula("Larga", "Acción", 180, "Director");
        ps.registrarPelicula("Otra", "Acción", 90, "Director");
        fs.asociarPeliculaASeccion(2026, TipoSeccion.COMPETENCIA_OFICIAL, ps.buscarPelicula("Larga"));
        fs.asociarPeliculaASeccion(2026, TipoSeccion.COMPETENCIA_OFICIAL, ps.buscarPelicula("Otra"));
        fs.registrarSala("SALA", 50);
        EntradaServicio es = new EntradaServicio(); es.setFestival(festival);
        es.programarFuncionPresencial(ps.buscarPelicula("Larga"), fs.buscarSala("SALA"), inicio, LocalTime.of(18, 0));
        assertThrows(() -> es.programarFuncionPresencial(ps.buscarPelicula("Otra"), fs.buscarSala("SALA"), inicio, LocalTime.of(19, 0)), "superposición por duración");
    }

    private static void testFechaFueraDeEdicion() {
        Festival festival = new Festival("Anime Festival Cinema");
        FestivalServicio fs = new FestivalServicio(festival, new ArrayList<>());
        LocalDate inicio = LocalDate.now().plusDays(2);
        fs.registrarEdicion(2026, "Buenos Aires", inicio, inicio.plusDays(2));
        fs.registrarSeccion(2026, TipoSeccion.COMPETENCIA_OFICIAL, "Premio", "Test");
        PeliculaServicio ps = new PeliculaServicio();
        ps.registrarDirector("Director", "director3@test.com", "Argentina");
        ps.registrarPelicula("Anime", "Acción", 90, "Director");
        fs.asociarPeliculaASeccion(2026, TipoSeccion.COMPETENCIA_OFICIAL, ps.buscarPelicula("Anime"));
        fs.registrarSala("SALA", 20);
        EntradaServicio es = new EntradaServicio(); es.setFestival(festival);
        assertThrows(() -> es.programarFuncionPresencial(ps.buscarPelicula("Anime"), fs.buscarSala("SALA"), inicio.plusDays(3), LocalTime.of(18, 0)), "fecha fuera de edición");
    }

    private static void testVentaDeFuncionFinalizada() {
        PeliculaServicio ps = new PeliculaServicio();
        ps.registrarDirector("Director", "director4@test.com", "Argentina");
        ps.registrarPelicula("Anime", "Acción", 90, "Director");
        EntradaServicio es = new EntradaServicio();
        es.registrarEspectador("Espectador", "espectador@test.com", "Argentina");
        Sala sala = new Sala("SALA", 10);
        FuncionPresencial pasada = new FuncionPresencial(LocalDate.now().minusDays(1), LocalTime.NOON, ps.buscarPelicula("Anime"), sala);
        assertThrows(() -> es.venderEntradaPresencial("Espectador", pasada, 1), "venta de función finalizada");
    }

    private static void testButacaDuplicada() {
        PeliculaServicio ps = new PeliculaServicio();
        ps.registrarDirector("Director", "director5@test.com", "Argentina");
        ps.registrarPelicula("Anime", "Acción", 90, "Director");
        EntradaServicio es = new EntradaServicio();
        es.registrarEspectador("Uno", "uno@test.com", "Argentina");
        es.registrarEspectador("Dos", "dos@test.com", "Argentina");
        FuncionPresencial f = new FuncionPresencial(LocalDate.now().plusDays(1), LocalTime.NOON, ps.buscarPelicula("Anime"), new Sala("SALA", 10));
        es.venderEntradaPresencial("Uno", f, 1);
        assertThrows(() -> es.venderEntradaPresencial("Dos", f, 1), "butaca duplicada");
    }

    private static void assertThrows(Runnable action, String nombre) {
        ejecutadas++;
        try { action.run(); }
        catch (FestivalException expected) { return; }
        throw new AssertionError("Falló: " + nombre + " (no lanzó FestivalException)");
    }
}
