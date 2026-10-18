package servicios;

import modelo.*;
import java.time.LocalDate;
import java.time.LocalTime;

/** Factory/Seeder: estado inicial completo para la demo web del festival. */
public class AnimeFestivalSeeder {
    private final FestivalServicio festivalServicio;
    private final PeliculaServicio peliculaServicio;
    private final EntradaServicio entradaServicio;

    public AnimeFestivalSeeder(FestivalServicio festivalServicio, PeliculaServicio peliculaServicio, EntradaServicio entradaServicio) {
        this.festivalServicio = festivalServicio;
        this.peliculaServicio = peliculaServicio;
        this.entradaServicio = entradaServicio;
    }

    public void cargar() {
        if (!peliculaServicio.getPeliculas().isEmpty()) return;

        LocalDate inicio = LocalDate.now().plusDays(2);
        LocalDate fin = inicio.plusDays(12);
        festivalServicio.registrarEdicion(2026, "Buenos Aires", inicio, fin);
        festivalServicio.registrarSeccion(2026, TipoSeccion.COMPETENCIA_OFICIAL,
                new Premio("Gran Premio Anime Festival", "Mejor largometraje de la competencia oficial."));
        festivalServicio.registrarSeccion(2026, TipoSeccion.CINE_INDEPENDIENTE,
                new Premio("Premio Mirai", "Reconocimiento a la propuesta independiente."));
        festivalServicio.registrarSeccion(2026, TipoSeccion.CORTOMETRAJE,
                new Premio("Premio Sakura", "Reconocimiento a la excelencia en formato corto."));
        festivalServicio.registrarSeccion(2026, TipoSeccion.NUEVOS_TALENTOS,
                new Premio("Premio Nuevas Voces", "Reconocimiento al talento emergente."));

        // Historial de ediciones del Anime Festival Cinema. Las ediciones pasadas
        // quedan disponibles para consulta, mientras 2026 concentra la programación activa.
        festivalServicio.registrarEdicion(2024, "Buenos Aires", LocalDate.of(2024, 9, 12), LocalDate.of(2024, 9, 22));
        festivalServicio.registrarSeccion(2024, TipoSeccion.COMPETENCIA_OFICIAL,
                new Premio("Gran Premio Anime 2024", "Mejor largometraje de la edición 2024."));
        festivalServicio.registrarSeccion(2024, TipoSeccion.CORTOMETRAJE,
                new Premio("Sakura Corto 2024", "Mejor obra breve de animación."));
        festivalServicio.registrarEdicion(2025, "Córdoba", LocalDate.of(2025, 10, 3), LocalDate.of(2025, 10, 13));
        festivalServicio.registrarSeccion(2025, TipoSeccion.COMPETENCIA_OFICIAL,
                new Premio("Gran Premio Anime 2025", "Mejor largometraje de la edición 2025."));
        festivalServicio.registrarSeccion(2025, TipoSeccion.NUEVOS_TALENTOS,
                new Premio("Premio Mirai 2025", "Reconocimiento a nuevos talentos."));

        crearSala("SAKURA 1", 48); crearSala("SAKURA 2", 64); crearSala("KAWAII 3", 80);
        crearSala("OTAKU 4", 100); crearSala("MIRAI 5", 120);

        String[][] directores = {
            {"Makoto Shinkai","shinkai@animefestival.com","Japón"},
            {"Hayao Miyazaki","miyazaki@animefestival.com","Japón"},
            {"Naoko Yamada","yamada@animefestival.com","Japón"},
            {"Satoshi Kon","kon@animefestival.com","Japón"},
            {"Katsuhiro Otomo","otomo@animefestival.com","Japón"},
            {"Haruo Sotozaki","sotozaki@animefestival.com","Japón"},
            {"Tatsuya Nagamine","nagamine@animefestival.com","Japón"},
            {"Takuya Igarashi","igarashi@animefestival.com","Japón"},
            {"Naoto Onishi","onishi@animefestival.com","Japón"}, {"Masato Jinbo","jinbo@animefestival.com","Japón"},
            {"Akiyuki Shinbo","shinbo@animefestival.com","Japón"}, {"Tatsuya Yoshihara","yoshihara@animefestival.com","Japón"},
            {"Tomoya Takahashi","takahashi@animefestival.com","Japón"}, {"Sōichi Masui","masui@animefestival.com","Japón"},
            {"Takashi Katagiri","katagiri@animefestival.com","Japón"}
        };
        for (String[] d : directores) peliculaServicio.registrarDirector(d[0], d[1], d[2]);

        crearPelicula("Demon Slayer: Infinity Castle", "Acción / Fantasía", 155, "Haruo Sotozaki", "Demon-slayer-kimetsu-no-yaiba-infinity-castle.jpg", "Tanjiro y los cazadores de demonios se enfrentan a una batalla decisiva dentro del Castillo Infinito.");
        crearPelicula("Kaguya-sama: Love is War", "Comedia / Romance", 90, "Naoto Onishi", "Kaguya-sama Love is War.jpg", "Kaguya y Miyuki convierten sus sentimientos en una divertida guerra de estrategias románticas.");
        crearPelicula("Las Quintillizas", "Romance / Comedia", 136, "Masato Jinbo", "Las Quintillizas.jpg", "Las cinco hermanas Nakano afrontan una nueva etapa mientras sus vínculos y sentimientos se ponen a prueba.");
        crearPelicula("Puella Magi Madoka Magica: Walpurgisnacht Rising", "Fantasía / Drama", 119, "Akiyuki Shinbo", "Puella Magi Madoka Magica Walpurgisnacht Rising.jpg", "Madoka y sus compañeras vuelven a un mundo de magia, sacrificio y decisiones que pueden alterar la realidad.");
        crearPelicula("SHIBOYUGI: Playing Death Games", "Acción / Supervivencia", 102, "Takuya Igarashi", "SHIBOYUGI-Playing-Death-Games-to-Put-Food-on-the-Table-44-CLOUDY-BEACH-poster-1098x1536.jpg", "Un grupo de jugadores participa en peligrosos desafíos donde la estrategia es la clave para sobrevivir.");
        crearPelicula("Chainsaw Man: Reze Arc", "Acción / Fantasía", 100, "Tatsuya Yoshihara", "chainsaw-man-reze.jpg", "Denji conoce a Reze y su vida cambia cuando el romance se mezcla con una amenaza explosiva.");
        crearPelicula("Dragon Ball Super: Broly", "Acción / Ciencia ficción", 100, "Tatsuya Nagamine", "dragon_ball_super_broly.jpg", "Goku y Vegeta conocen a Broly en un enfrentamiento que revela nuevos capítulos de la historia Saiyajin.");
        crearPelicula("Sailor Moon Cosmos", "Fantasía / Aventura", 160, "Tomoya Takahashi", "gekijoban-bishojo-senshi-sailor-moon-cosmos.jpg", "Las Sailor Guardians libran su batalla final para proteger el universo y el futuro de quienes aman.");
        crearPelicula("Rascal Does Not Dream of a Bunny Girl Senpai", "Drama / Romance", 90, "Sōichi Masui", "seishun_buta_yarou_wa_bunny_girl.jpg", "Sakuta intenta comprender fenómenos extraños mientras acompaña a Mai y a sus amigos durante la adolescencia.");
        crearPelicula("SPY x FAMILY CODE: White", "Comedia / Acción", 110, "Takashi Katagiri", "spy-x-family-code-white.jpg", "La familia Forger emprende una misión de vacaciones que rápidamente se convierte en una aventura internacional.");

        // Actores: relación muchos-a-muchos para demostrar el modelo OO.
        registrarActor("Natsuki Hanae", "Japón"); registrarActor("Akari Kito", "Japón");
        registrarActor("Aoi Koga", "Japón"); registrarActor("Makoto Furukawa", "Japón");
        registrarActor("Ayane Sakura", "Japón"); registrarActor("Kana Hanazawa", "Japón");
        registrarActor("Kensho Ono", "Japón"); registrarActor("Saori Hayami", "Japón");
        registrarActor("Kaito Ishikawa", "Japón"); registrarActor("Rie Takahashi", "Japón");
        registrarActor("Megumi Han", "Japón"); registrarActor("Nobuhiko Okamoto", "Japón");
        asociarActor("Natsuki Hanae", "Demon Slayer: Infinity Castle"); asociarActor("Akari Kito", "Demon Slayer: Infinity Castle");
        asociarActor("Aoi Koga", "Kaguya-sama: Love is War"); asociarActor("Makoto Furukawa", "Kaguya-sama: Love is War");
        asociarActor("Ayane Sakura", "Las Quintillizas"); asociarActor("Kana Hanazawa", "Las Quintillizas");
        asociarActor("Natsuki Hanae", "Chainsaw Man: Reze Arc"); asociarActor("Kana Hanazawa", "Sailor Moon Cosmos");
        asociarActor("Kensho Ono", "Demon Slayer: Infinity Castle"); asociarActor("Saori Hayami", "SPY x FAMILY CODE: White");
        asociarActor("Kaito Ishikawa", "Rascal Does Not Dream of a Bunny Girl Senpai"); asociarActor("Rie Takahashi", "Puella Magi Madoka Magica: Walpurgisnacht Rising");
        asociarActor("Megumi Han", "Puella Magi Madoka Magica: Walpurgisnacht Rising"); asociarActor("Nobuhiko Okamoto", "Dragon Ball Super: Broly");

        peliculaServicio.registrarJurado("IA Jury", "ia@animefestival.com", "Internacional");
        peliculaServicio.registrarJurado("Akira Tanaka", "tanaka@animefestival.com", "Japón");
        peliculaServicio.registrarJurado("María Fernández", "maria@animefestival.com", "Argentina");
        String[] titulos = {"Demon Slayer: Infinity Castle","Kaguya-sama: Love is War","Las Quintillizas","Puella Magi Madoka Magica: Walpurgisnacht Rising","SHIBOYUGI: Playing Death Games","Chainsaw Man: Reze Arc","Dragon Ball Super: Broly","Sailor Moon Cosmos","Rascal Does Not Dream of a Bunny Girl Senpai","SPY x FAMILY CODE: White"};
        double[] notas = {9.8,9.1,8.9,9.6,8.7,9.4,9.3,9.2,8.8,9.5};
        for (int i=0;i<titulos.length;i++) {
            puntuar("IA Jury", titulos[i], notas[i]);
            puntuar("Akira Tanaka", titulos[i], Math.max(0, notas[i]-0.2));
        }

        // Asociaciones a secciones.
        for (int i=0;i<titulos.length;i++) {
            TipoSeccion tipo = i < 4 ? TipoSeccion.COMPETENCIA_OFICIAL : (i < 6 ? TipoSeccion.CINE_INDEPENDIENTE : (i < 8 ? TipoSeccion.CORTOMETRAJE : TipoSeccion.NUEVOS_TALENTOS));
            festivalServicio.asociarPeliculaASeccion(2026, tipo, peliculaServicio.buscarPelicula(titulos[i]));
        }

        // Funciones presenciales internacionales.
        programar(titulos[0], "SAKURA 1", inicio, 18, "Buenos Aires", "Argentina");
        programar(titulos[0], "SAKURA 2", inicio.plusDays(3), 21, "Tokio", "Japón");
        programar(titulos[1], "KAWAII 3", inicio.plusDays(1), 19, "Madrid", "España");
        programar(titulos[2], "SAKURA 1", inicio.plusDays(2), 20, "París", "Francia");
        programar(titulos[3], "MIRAI 5", inicio.plusDays(4), 17, "Londres", "Reino Unido");
        programar(titulos[4], "OTAKU 4", inicio.plusDays(5), 22, "Seúl", "Corea del Sur");
        programar(titulos[5], "MIRAI 5", inicio.plusDays(6), 20, "Nueva York", "Estados Unidos");
        programar(titulos[6], "KAWAII 3", inicio.plusDays(7), 18, "Buenos Aires", "Argentina");
        programar(titulos[7], "SAKURA 2", inicio.plusDays(8), 19, "Tokio", "Japón");
        programar(titulos[8], "OTAKU 4", inicio.plusDays(9), 21, "Santiago", "Chile");
        programar(titulos[9], "SAKURA 1", inicio.plusDays(10), 20, "Los Ángeles", "Estados Unidos");

        // Funciones de streaming, exigidas por el enunciado.
        entradaServicio.programarFuncionStreaming(peliculaServicio.buscarPelicula(titulos[0]), "https://animefestival.local/stream/infinity", 500, inicio.plusDays(2), LocalTime.of(22,0), "Online", "Internacional");
        entradaServicio.programarFuncionStreaming(peliculaServicio.buscarPelicula(titulos[5]), "https://animefestival.local/stream/reze", 300, inicio.plusDays(6), LocalTime.of(23,0), "Online", "Internacional");
        entradaServicio.programarFuncionStreaming(peliculaServicio.buscarPelicula(titulos[9]), "https://animefestival.local/stream/spy-family", 400, inicio.plusDays(11), LocalTime.of(18,0), "Online", "Internacional");

        // Datos de demostración para que el panel web nunca arranque con tablas vacías.
        String[][] espectadores = {
            {"Valentina Rossi","valentina.rossi@demo.com","Argentina"}, {"Lucas Nakamura","lucas.nakamura@demo.com","Japón"},
            {"Sofía García","sofia.garcia@demo.com","Argentina"}, {"Mateo Fernández","mateo.fernandez@demo.com","España"},
            {"Camila Sato","camila.sato@demo.com","Chile"}, {"Nicolás Tanaka","nicolas.tanaka@demo.com","Argentina"},
            {"Emma Suzuki","emma.suzuki@demo.com","Japón"}, {"Tomás Pereira","tomas.pereira@demo.com","Uruguay"},
            {"Lucía Méndez","lucia.mendez@demo.com","México"}, {"Diego Watanabe","diego.watanabe@demo.com","Perú"},
            {"Agustina López","agustina.lopez@demo.com","Argentina"}, {"Bruno Kato","bruno.kato@demo.com","Brasil"}
        };
        for (String[] e : espectadores) entradaServicio.registrarEspectador(e[0], e[1], e[2]);

        // Venta inicial de entradas: mezcla de funciones presenciales y streaming,
        // con diferentes tarifas para alimentar las tablas y estadísticas.
        venderDemo("Valentina Rossi", titulos[0], 1, TipoTarifa.GENERAL);
        venderDemo("Lucas Nakamura", titulos[1], 2, TipoTarifa.ESTUDIANTE);
        venderDemo("Sofía García", titulos[2], 3, TipoTarifa.GENERAL);
        venderDemo("Mateo Fernández", titulos[3], 4, TipoTarifa.JUBILADO);
        venderDemo("Camila Sato", titulos[4], 5, TipoTarifa.ESTUDIANTE);
        venderDemo("Nicolás Tanaka", titulos[5], 6, TipoTarifa.GENERAL);
        venderDemo("Emma Suzuki", titulos[6], 7, TipoTarifa.ESTUDIANTE);
        venderDemo("Tomás Pereira", titulos[7], 8, TipoTarifa.GENERAL);
        venderDemo("Lucía Méndez", titulos[8], 9, TipoTarifa.GENERAL);
        venderDemo("Diego Watanabe", titulos[9], 10, TipoTarifa.ESTUDIANTE);
        venderDemoStreaming("Agustina López", titulos[0], TipoTarifa.GENERAL);
        venderDemoStreaming("Bruno Kato", titulos[5], TipoTarifa.ESTUDIANTE);
        venderDemoStreaming("Valentina Rossi", titulos[9], TipoTarifa.JUBILADO);

        // Abonos / bonos de los tres tipos.
        entradaServicio.comprarAbonoTresDias("Valentina Rossi", TipoTarifa.GENERAL, java.util.List.of(inicio, inicio.plusDays(1), inicio.plusDays(2)));
        entradaServicio.comprarAbonoFull("Lucas Nakamura", TipoTarifa.ESTUDIANTE);
        entradaServicio.comprarAbonoFull("Sofía García", TipoTarifa.GENERAL);
        entradaServicio.comprarAbonoStreaming("Camila Sato", TipoTarifa.ESTUDIANTE);
        entradaServicio.comprarAbonoStreaming("Mateo Fernández", TipoTarifa.JUBILADO);
        entradaServicio.comprarAbonoTresDias("Emma Suzuki", TipoTarifa.ESTUDIANTE, java.util.List.of(inicio.plusDays(3), inicio.plusDays(4), inicio.plusDays(5)));

        // Algunas reservas con abono para demostrar también la relación abono -> entrada.
        Abono fullLucas = entradaServicio.getAbonos().stream().filter(a -> a.getEspectador().getNombre().equals("Lucas Nakamura")).findFirst().orElse(null);
        if (fullLucas != null) reservarDemoConAbono(fullLucas, titulos[6], 11);
        Abono fullSofia = entradaServicio.getAbonos().stream().filter(a -> a.getEspectador().getNombre().equals("Sofía García")).findFirst().orElse(null);
        if (fullSofia != null) reservarDemoConAbono(fullSofia, titulos[7], 12);
        Abono streamCamila = entradaServicio.getAbonos().stream().filter(a -> a.getEspectador().getNombre().equals("Camila Sato")).findFirst().orElse(null);
        if (streamCamila != null) reservarDemoStreamingConAbono(streamCamila, titulos[5]);

        // Votos del público de demostración. Cada votante posee una entrada para la película.
        votarDemo("Valentina Rossi", titulos[0], 5); votarDemo("Lucas Nakamura", titulos[1], 5);
        votarDemo("Sofía García", titulos[2], 4); votarDemo("Mateo Fernández", titulos[3], 5);
        votarDemo("Camila Sato", titulos[4], 4); votarDemo("Nicolás Tanaka", titulos[5], 5);
        votarDemo("Emma Suzuki", titulos[6], 5); votarDemo("Tomás Pereira", titulos[7], 4);
        votarDemo("Lucía Méndez", titulos[8], 4); votarDemo("Diego Watanabe", titulos[9], 5);
        votarDemo("Agustina López", titulos[0], 4); votarDemo("Bruno Kato", titulos[5], 5);

        // Una segunda ronda de evaluaciones para que el panel muestre variedad de jurados.
        double[] segunda = {9.5, 8.9, 9.0, 9.4, 8.5, 9.1, 9.0, 9.3, 8.6, 9.2};
        for (int i=0;i<titulos.length;i++) puntuar("María Fernández", titulos[i], segunda[i]);
    }

    private void venderDemo(String espectador, String pelicula, int butaca, TipoTarifa tarifa) {
        for (Funcion f : entradaServicio.getFunciones()) {
            if (f instanceof FuncionPresencial fp && f.getPelicula().getTitulo().equals(pelicula)) {
                entradaServicio.venderEntradaPresencial(espectador, fp, butaca, tarifa); return;
            }
        }
    }
    private void venderDemoStreaming(String espectador, String pelicula, TipoTarifa tarifa) {
        for (Funcion f : entradaServicio.getFunciones()) {
            if (f instanceof FuncionStreaming fs && f.getPelicula().getTitulo().equals(pelicula)) {
                entradaServicio.venderEntradaStreaming(espectador, fs, tarifa); return;
            }
        }
    }
    private void reservarDemoConAbono(Abono abono, String pelicula, int butaca) {
        for (Funcion f : entradaServicio.getFunciones()) {
            if (f instanceof FuncionPresencial fp && f.getPelicula().getTitulo().equals(pelicula) && abono.cubre(f)) {
                entradaServicio.reservarPresencialConAbono(abono.getCodigo(), fp, butaca); return;
            }
        }
    }
    private void reservarDemoStreamingConAbono(Abono abono, String pelicula) {
        for (Funcion f : entradaServicio.getFunciones()) {
            if (f instanceof FuncionStreaming fs && f.getPelicula().getTitulo().equals(pelicula) && abono.cubre(f)) {
                entradaServicio.reservarStreamingConAbono(abono.getCodigo(), fs); return;
            }
        }
    }
    private void votarDemo(String espectador, String pelicula, int puntaje) {
        peliculaServicio.registrarVotoPublico(entradaServicio.buscarEspectador(espectador), pelicula, puntaje);
    }

    private void crearSala(String nombre, int capacidad) { if (festivalServicio.getSalas().stream().noneMatch(s -> s.getNombre().equals(nombre))) festivalServicio.registrarSala(nombre, capacidad); }
    private void crearPelicula(String titulo, String genero, int duracion, String director, String imagen, String descripcion) {
        peliculaServicio.registrarPelicula(titulo, genero, duracion, director);
        Pelicula p = peliculaServicio.buscarPelicula(titulo); p.setAnime(true); p.setImagenUrl("/assets/posters/" + imagen); p.setDescripcion(descripcion);
    }
    private void registrarActor(String nombre, String nacionalidad) { peliculaServicio.registrarActor(nombre, nombre.toLowerCase().replace(" ", ".")+"@animefestival.com", nacionalidad); }
    private void asociarActor(String actor, String pelicula) { peliculaServicio.asociarActorAPelicula(actor, pelicula); }
    private void puntuar(String jurado, String pelicula, double nota) { peliculaServicio.registrarEvaluacion(jurado, pelicula, nota); }
    private void programar(String pelicula, String sala, LocalDate fecha, int hora, String ciudad, String pais) { entradaServicio.programarFuncionPresencial(peliculaServicio.buscarPelicula(pelicula), festivalServicio.buscarSala(sala), fecha, LocalTime.of(hora,0), ciudad, pais); }
}
