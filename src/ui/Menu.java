package ui;

import excepciones.FestivalException;
import modelo.*;
import servicios.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private FestivalServicio festivalServicio;
    private PeliculaServicio peliculaServicio;
    private EntradaServicio entradaServicio;
    private Scanner scanner;

    public Menu(FestivalServicio festivalServicio, PeliculaServicio peliculaServicio, EntradaServicio entradaServicio) {
        this.festivalServicio = festivalServicio;
        this.peliculaServicio = peliculaServicio;
        this.entradaServicio = entradaServicio;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcion;
        do {
            mostrarMenuPrincipal();
            opcion = leerEntero();
            switch (opcion) {
                case 1 -> menuFestival();
                case 2 -> menuPeliculas();
                case 3 -> menuFunciones();
                case 4 -> menuEntradas();
                case 5 -> menuEvaluaciones();
                case 0 -> System.out.println("Hasta luego!");
                default -> System.out.println("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n===== SISTEMA DE GESTIÓN FESTIVAL DE CINE =====");
        System.out.println("1. Gestión del festival");
        System.out.println("2. Gestión de películas");
        System.out.println("3. Gestión de funciones");
        System.out.println("4. Venta de entradas");
        System.out.println("5. Evaluaciones y premios");
        System.out.println("0. Salir");
        System.out.print("Seleccione una opción: ");
    }

    // ========== FESTIVAL ==========

    private void menuFestival() {
        System.out.println("\n--- Gestión del Festival ---");
        System.out.println("1. Registrar edición");
        System.out.println("2. Registrar sección");
        System.out.println("3. Registrar sala");
        System.out.println("4. Ver ediciones");
        System.out.println("5. Ver salas");
        System.out.println("0. Volver al menu anterior");
        System.out.print("Seleccione una opción: ");
        int opcion = leerEntero();
        try {
            switch (opcion) {
                case 1 -> registrarEdicion();
                case 2 -> registrarSeccion();
                case 3 -> registrarSala();
                case 4 -> verEdiciones();
                case 5 -> verSalas();
                case 0 -> { }
                default -> System.out.println("Opcion invalida.");
            }
        } catch (FestivalException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registrarEdicion() {
        int anio;
        do {
            System.out.print("Año (mayor a 2000): ");
            anio = leerEntero();
            if (anio < 2000) {
                System.out.println("Error: el año debe ser mayor a 2000.");
            }
        } while (anio < 2000);
        System.out.print("Ciudad: ");
        String ciudad = scanner.nextLine();
        LocalDate fechaInicio = leerFecha("Fecha inicio (YYYY-MM-DD): ");
        LocalDate fechaFin = leerFecha("Fecha fin (YYYY-MM-DD): ");
        festivalServicio.registrarEdicion(anio, ciudad, fechaInicio, fechaFin);
        System.out.println("Edición registrada correctamente.");
    }

    private void registrarSeccion() {
        System.out.print("Año de la edición: ");
        int anio = leerEntero();
        List<String> tipos = new ArrayList<>();
        for (TipoSeccion t : TipoSeccion.values()) {
            tipos.add(t.name());
        }
        String tipoStr = seleccionarPorNumero("Tipos de sección disponibles:", tipos);
        TipoSeccion tipo = TipoSeccion.valueOf(tipoStr.toUpperCase());
        System.out.print("Nombre del premio: ");
        String nombrePremio = scanner.nextLine();
        System.out.print("Descripción del premio: ");
        String descPremio = scanner.nextLine();
        festivalServicio.registrarSeccion(anio, tipo, new Premio(nombrePremio, descPremio));
        System.out.println("Sección registrada correctamente.");
    }

    private void registrarSala() {
        System.out.print("Nombre de la sala: ");
        String nombre = scanner.nextLine();
        int capacidad;
        do {
            System.out.print("Capacidad (mayor a 0): ");
            capacidad = leerEntero();
            if (capacidad <= 0) {
                System.out.println("Error: la capacidad debe ser mayor a 0.");
            }
        } while (capacidad <= 0);
        festivalServicio.registrarSala(nombre, capacidad);
        System.out.println("Sala registrada correctamente.");
    }

    private void verEdiciones() {
        List<Edicion> ediciones = festivalServicio.getEdiciones();
        if (ediciones.isEmpty()) {
            System.out.println("No hay ediciones registradas.");
        } else {
            for (Edicion e : ediciones) {
                System.out.println(e);
            }
        }
    }

    private void verSalas() {
        List<Sala> salas = festivalServicio.getSalas();
        if (salas.isEmpty()) {
            System.out.println("No hay salas registradas.");
        } else {
            for (Sala s : salas) {
                System.out.println(s);
            }
        }
    }

    // ========== PELICULAS ==========

    private void menuPeliculas() {
        System.out.println("\n--- Gestión de Películas ---");
        System.out.println("1. Registrar director");
        System.out.println("2. Registrar actor");
        System.out.println("3. Registrar película");
        System.out.println("4. Asociar actor a película");
        System.out.println("5. Ver películas");
        System.out.println("0. Volver al menu anterior");
        System.out.print("Seleccione una opción: ");
        int opcion = leerEntero();
        try {
            switch (opcion) {
                case 1 -> registrarDirector();
                case 2 -> registrarActor();
                case 3 -> registrarPelicula();
                case 4 -> asociarActor();
                case 5 -> verPeliculas();
                case 0 -> { }
                default -> System.out.println("Opcion invalida.");
            }
        } catch (FestivalException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registrarDirector() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Nacionalidad: ");
        String nacionalidad = scanner.nextLine();
        peliculaServicio.registrarDirector(nombre, email, nacionalidad);
        System.out.println("Director registrado correctamente.");
    }

    private void registrarActor() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Nacionalidad: ");
        String nacionalidad = scanner.nextLine();
        peliculaServicio.registrarActor(nombre, email, nacionalidad);
        System.out.println("Actor registrado correctamente.");
    }

    private void registrarPelicula() {
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        System.out.print("Género: ");
        String genero = scanner.nextLine();
        int duracion;
        do {
            System.out.print("Duración en minutos (mayor a 0): ");
            duracion = leerEntero();
            if (duracion <= 0) {
                System.out.println("Error: la duración debe ser mayor a 0.");
            }
        } while (duracion <= 0);
        if (peliculaServicio.getDirectores().isEmpty()) {
            System.out.println("No hay directores registrados. Registre uno primero.");
            return;
        }
        List<String> nombresDirectores = new ArrayList<>();
        for (Director d : peliculaServicio.getDirectores()) {
            nombresDirectores.add(d.getNombre());
        }
        String director = seleccionarPorNumero("Directores disponibles:", nombresDirectores);
        peliculaServicio.registrarPelicula(titulo, genero, duracion, director);
        System.out.println("Película registrada correctamente.");
    }

    private void asociarActor() {
        if (peliculaServicio.getActores().isEmpty()) {
            System.out.println("No hay actores registrados. Registre uno primero.");
            return;
        }
        if (peliculaServicio.getPeliculas().isEmpty()) {
            System.out.println("No hay películas registradas. Registre una primero.");
            return;
        }
        List<String> nombresActores = new ArrayList<>();
        for (Actor a : peliculaServicio.getActores()) {
            nombresActores.add(a.getNombre());
        }
        String actor = seleccionarPorNumero("Actores disponibles:", nombresActores);
        List<String> nombresPeliculas = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            nombresPeliculas.add(p.getTitulo());
        }
        String pelicula = seleccionarPorNumero("Películas disponibles:", nombresPeliculas);
        peliculaServicio.asociarActorAPelicula(actor, pelicula);
        System.out.println("Actor asociado correctamente.");
    }

    private void verPeliculas() {
        List<Pelicula> peliculas = peliculaServicio.getPeliculas();
        if (peliculas.isEmpty()) {
            System.out.println("No hay películas registradas.");
        } else {
            for (Pelicula p : peliculas) {
                System.out.println(p);
            }
        }
    }

    // ========== FUNCIONES ==========

    private void menuFunciones() {
        System.out.println("\n--- Gestión de Funciones ---");
        System.out.println("1. Programar función presencial");
        System.out.println("2. Programar función streaming");
        System.out.println("3. Ver funciones");
        System.out.println("0. Volver al menu anterior");
        System.out.print("Seleccione una opción: ");
        int opcion = leerEntero();
        try {
            switch (opcion) {
                case 1 -> programarFuncionPresencial();
                case 2 -> programarFuncionStreaming();
                case 3 -> verFunciones();
                case 0 -> { }
                default -> System.out.println("Opcion invalida.");
            }
        } catch (FestivalException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void programarFuncionPresencial() {
        if (peliculaServicio.getPeliculas().isEmpty()) {
            System.out.println("No hay películas registradas. Registre una primero.");
            return;
        }
        if (festivalServicio.getSalas().isEmpty()) {
            System.out.println("No hay salas registradas. Registre una primero.");
            return;
        }
        List<String> nombresPeliculas = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            nombresPeliculas.add(p.getTitulo());
        }
        String titulo = seleccionarPorNumero("Películas disponibles:", nombresPeliculas);
        List<String> nombresSalas = new ArrayList<>();
        for (Sala s : festivalServicio.getSalas()) {
            nombresSalas.add(s.getNombre());
        }
        String sala = seleccionarPorNumero("Salas disponibles:", nombresSalas);
        LocalDate fecha = leerFecha("Fecha (YYYY-MM-DD): ");
        LocalTime horario = leerHorario("Horario (HH:MM): ");
        Pelicula pelicula = peliculaServicio.buscarPelicula(titulo);
        Sala salaObj = festivalServicio.buscarSala(sala);
        entradaServicio.programarFuncionPresencial(pelicula, salaObj, fecha, horario);
        System.out.println("Función presencial programada correctamente.");
    }

    private void programarFuncionStreaming() {
        if (peliculaServicio.getPeliculas().isEmpty()) {
            System.out.println("No hay películas registradas. Registre una primero.");
            return;
        }
        List<String> nombresPeliculas = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            nombresPeliculas.add(p.getTitulo());
        }
        String titulo = seleccionarPorNumero("Películas disponibles:", nombresPeliculas);
        System.out.print("Enlace: ");
        String enlace = scanner.nextLine();
        int capacidad;
        do {
            System.out.print("Capacidad virtual (mayor a 0): ");
            capacidad = leerEntero();
            if (capacidad <= 0) {
                System.out.println("Error: la capacidad debe ser mayor a 0.");
            }
        } while (capacidad <= 0);
        LocalDate fecha = leerFecha("Fecha (YYYY-MM-DD): ");
        LocalTime horario = leerHorario("Horario (HH:MM): ");
        Pelicula pelicula = peliculaServicio.buscarPelicula(titulo);
        entradaServicio.programarFuncionStreaming(pelicula, enlace, capacidad, fecha, horario);
        System.out.println("Función streaming programada correctamente.");
    }

    private void verFunciones() {
        List<Funcion> funciones = entradaServicio.getFunciones();
        if (funciones.isEmpty()) {
            System.out.println("No hay funciones programadas.");
        } else {
            for (Funcion f : funciones) {
                System.out.println(f);
            }
        }
    }

    // ========== ENTRADAS ==========

    private void menuEntradas() {
        System.out.println("\n--- Venta de Entradas ---");
        System.out.println("1. Registrar espectador");
        System.out.println("2. Vender entrada presencial");
        System.out.println("3. Vender entrada streaming");
        System.out.println("4. Ver ocupación de función");
        System.out.println("0. Volver al menu anterior");
        System.out.print("Seleccione una opción: ");
        int opcion = leerEntero();
        try {
            switch (opcion) {
                case 1 -> registrarEspectador();
                case 2 -> venderEntradaPresencial();
                case 3 -> venderEntradaStreaming();
                case 4 -> verOcupacion();
                case 0 -> { }
                default -> System.out.println("Opcion invalida.");
            }
        } catch (FestivalException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registrarEspectador() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Nacionalidad: ");
        String nacionalidad = scanner.nextLine();
        entradaServicio.registrarEspectador(nombre, email, nacionalidad);
        System.out.println("Espectador registrado correctamente.");
    }

    private Espectador buscarORegistrarEspectador() {
        System.out.print("Nombre del espectador: ");
        String nombre = scanner.nextLine();
        try {
            return entradaServicio.buscarEspectador(nombre);
        } catch (FestivalException e) {
            System.out.println("Espectador no encontrado. ¿Desea registrarlo? (s/n): ");
            String respuesta = scanner.nextLine();
            if (respuesta.equalsIgnoreCase("s")) {
                System.out.print("Email: ");
                String email = scanner.nextLine();
                System.out.print("Nacionalidad: ");
                String nacionalidad = scanner.nextLine();
                entradaServicio.registrarEspectador(nombre, email, nacionalidad);
                return entradaServicio.buscarEspectador(nombre);
            }
            return null;
        }
    }

    private Funcion seleccionarFuncion(String tipo) {
        List<Funcion> funcionesFiltradas = new ArrayList<>();
        for (Funcion f : entradaServicio.getFunciones()) {
            if (tipo.equals("presencial") && f instanceof FuncionPresencial) {
                funcionesFiltradas.add(f);
            } else if (tipo.equals("streaming") && f instanceof FuncionStreaming) {
                funcionesFiltradas.add(f);
            } else if (tipo.equals("todas")) {
                funcionesFiltradas.add(f);
            }
        }
        if (funcionesFiltradas.isEmpty()) return null;

        System.out.println("Funciones disponibles:");
        for (int i = 0; i < funcionesFiltradas.size(); i++) {
            Funcion f = funcionesFiltradas.get(i);
            System.out.println("  " + (i + 1) + ". " + f.getPelicula().getTitulo()
                    + " - " + f.getFecha() + " - " + f.getHorario());
        }
        int seleccion;
        do {
            System.out.print("Ingrese el número de la opción: ");
            seleccion = leerEntero();
            if (seleccion < 1 || seleccion > funcionesFiltradas.size()) {
                System.out.println("Opción inválida, ingrese un número entre 1 y " + funcionesFiltradas.size() + ".");
            }
        } while (seleccion < 1 || seleccion > funcionesFiltradas.size());
        return funcionesFiltradas.get(seleccion - 1);
    }

    private void venderEntradaPresencial() {
        if (entradaServicio.getFunciones().isEmpty()) {
            System.out.println("No hay funciones programadas. Programe una primero.");
            return;
        }
        Espectador espectador = buscarORegistrarEspectador();
        if (espectador == null) return;

        Funcion funcion = seleccionarFuncion("presencial");
        if (funcion == null) {
            System.out.println("No hay funciones presenciales programadas.");
            return;
        }

        EntradaPresencial entrada = null;
        while (entrada == null) {
            int butaca;
            do {
                System.out.print("Número de butaca (1-" + ((FuncionPresencial) funcion).getSala().getCantidadButacas() + "): ");
                butaca = leerEntero();
                if (butaca <= 0) {
                    System.out.println("Error: el número de butaca debe ser mayor a 0.");
                }
            } while (butaca <= 0);
            try {
                entrada = entradaServicio.venderEntradaPresencial(espectador.getNombre(), (FuncionPresencial) funcion, butaca);
            } catch (FestivalException e) {
                System.out.println("Error: " + e.getMessage() + " Intente con otra butaca.");
            }
        }
        System.out.println("Entrada vendida: " + entrada);
    }

    private void venderEntradaStreaming() {
        if (entradaServicio.getFunciones().isEmpty()) {
            System.out.println("No hay funciones programadas. Programe una primero.");
            return;
        }
        Espectador espectador = buscarORegistrarEspectador();
        if (espectador == null) return;

        Funcion funcion = seleccionarFuncion("streaming");
        if (funcion == null) {
            System.out.println("No hay funciones streaming programadas.");
            return;
        }
        EntradaStreaming entrada = entradaServicio.venderEntradaStreaming(espectador.getNombre(), (FuncionStreaming) funcion);
        System.out.println("Entrada vendida: " + entrada);
    }

    private void verOcupacion() {
        if (entradaServicio.getFunciones().isEmpty()) {
            System.out.println("No hay funciones programadas.");
            return;
        }
        Funcion funcion = seleccionarFuncion("todas");
        if (funcion == null) return;
        System.out.printf("Ocupación: %.2f%%%n", funcion.calcularOcupacion());
        if (funcion instanceof FuncionPresencial) {
            FuncionPresencial fp = (FuncionPresencial) funcion;
            int disponibles = fp.getSala().getCantidadButacas() - fp.getButacasOcupadas().size();
            System.out.println("Butacas disponibles: " + disponibles + " de " + fp.getSala().getCantidadButacas());
        } else if (funcion instanceof FuncionStreaming) {
            FuncionStreaming fs = (FuncionStreaming) funcion;
            int disponibles = fs.getCapacidadVirtual() - fs.getEspectadoresConectados();
            System.out.println("Lugares disponibles: " + disponibles + " de " + fs.getCapacidadVirtual());
        }
    }

    // ========== EVALUACIONES ==========

    private void menuEvaluaciones() {
        System.out.println("\n--- Evaluaciones y Premios ---");
        System.out.println("1. Registrar jurado");
        System.out.println("2. Registrar evaluación");
        System.out.println("3. Ver promedio de película");
        System.out.println("4. Determinar ganadora por sección");
        System.out.println("0. Volver al menu anterior");
        System.out.print("Seleccione una opción: ");
        int opcion = leerEntero();
        try {
            switch (opcion) {
                case 1 -> registrarJurado();
                case 2 -> registrarEvaluacion();
                case 3 -> verPromedio();
                case 4 -> determinarGanadora();
                case 0 -> { }
                default -> System.out.println("Opcion invalida.");
            }
        } catch (FestivalException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void registrarJurado() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Nacionalidad: ");
        String nacionalidad = scanner.nextLine();
        peliculaServicio.registrarJurado(nombre, email, nacionalidad);
        System.out.println("Jurado registrado correctamente.");
    }

    private void registrarEvaluacion() {
        if (peliculaServicio.getJurados().isEmpty()) {
            System.out.println("No hay jurados registrados. Registre uno primero.");
            return;
        }
        if (peliculaServicio.getPeliculas().isEmpty()) {
            System.out.println("No hay películas registradas. Registre una primero.");
            return;
        }
        List<String> nombresJurados = new ArrayList<>();
        for (Jurado j : peliculaServicio.getJurados()) {
            nombresJurados.add(j.getNombre());
        }
        String jurado = seleccionarPorNumero("Jurados disponibles:", nombresJurados);
        List<String> nombresPeliculas = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            nombresPeliculas.add(p.getTitulo());
        }
        String pelicula = seleccionarPorNumero("Películas disponibles:", nombresPeliculas);
        double puntaje = leerDouble("Puntaje (0-10): ", 0, 10);
        peliculaServicio.registrarEvaluacion(jurado, pelicula, puntaje);
        System.out.println("Evaluación registrada correctamente.");
    }

    private void verPromedio() {
        if (peliculaServicio.getPeliculas().isEmpty()) {
            System.out.println("No hay películas registradas.");
            return;
        }
        List<String> nombresPeliculas = new ArrayList<>();
        for (Pelicula p : peliculaServicio.getPeliculas()) {
            nombresPeliculas.add(p.getTitulo());
        }
        String titulo = seleccionarPorNumero("Películas disponibles:", nombresPeliculas);
        Pelicula pelicula = peliculaServicio.buscarPelicula(titulo);
        System.out.printf("Promedio de %s: %.2f%n", titulo, pelicula.calcularPromedio());
    }

    private void determinarGanadora() {
        List<Edicion> ediciones = festivalServicio.getEdiciones();
        if (ediciones.isEmpty()) {
            System.out.println("No hay ediciones registradas.");
            return;
        }
        System.out.println("Ediciones disponibles:");
        for (Edicion e : ediciones) {
            System.out.println("  - " + e.getAnio() + " (" + e.getCiudad() + ")");
        }
        int anio;
        boolean anioValido;
        do {
            System.out.print("Año de la edición: ");
            anio = leerEntero();
            anioValido = false;
            for (Edicion e : ediciones) {
                if (e.getAnio() == anio) {
                    anioValido = true;
                    break;
                }
            }
            if (!anioValido) {
                System.out.println("Año inválido, ingrese uno de la lista.");
            }
        } while (!anioValido);

        List<String> tipos = new ArrayList<>();
        for (TipoSeccion t : TipoSeccion.values()) {
            tipos.add(t.name());
        }
        String tipoStr = seleccionarPorNumero("Tipos de sección disponibles:", tipos);
        TipoSeccion tipo = TipoSeccion.valueOf(tipoStr.toUpperCase());
        Seccion seccion = festivalServicio.buscarSeccion(anio, tipo);
        Pelicula ganadora = peliculaServicio.determinarGanadora(seccion);
        if (ganadora == null) {
            System.out.println("No hay películas en esta sección.");
        } else {
            System.out.println("Ganadora: " + ganadora);
            System.out.println("Premio: " + seccion.getPremio());
        }
    }

    // ========== UTILIDADES ==========

    private int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Valor inválido, ingrese un número: ");
            }
        }
    }

    private double leerDouble(String mensaje, double min, double max) {
        double valor = min - 1;
        do {
            try {
                System.out.print(mensaje);
                valor = Double.parseDouble(scanner.nextLine());
                if (valor < min || valor > max) {
                    System.out.println("Valor inválido, debe estar entre " + min + " y " + max + ".");
                    valor = min - 1;
                }
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido, ingrese un número.");
            }
        } while (valor < min || valor > max);
        return valor;
    }

    private LocalDate leerFecha(String mensaje) {
        LocalDate fecha = null;
        do {
            try {
                System.out.print(mensaje);
                fecha = LocalDate.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Fecha inválida, ingrese el formato correcto (YYYY-MM-DD).");
            }
        } while (fecha == null);
        return fecha;
    }

    private LocalTime leerHorario(String mensaje) {
        LocalTime horario = null;
        do {
            try {
                System.out.print(mensaje);
                horario = LocalTime.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Horario inválido, ingrese el formato correcto (HH:MM).");
            }
        } while (horario == null);
        return horario;
    }

    private String seleccionarPorNumero(String mensaje, List<String> opciones) {
        System.out.println(mensaje);
        for (int i = 0; i < opciones.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + opciones.get(i));
        }
        int seleccion;
        do {
            System.out.print("Ingrese el número de la opción: ");
            seleccion = leerEntero();
            if (seleccion < 1 || seleccion > opciones.size()) {
                System.out.println("Opción inválida, ingrese un número entre 1 y " + opciones.size() + ".");
            }
        } while (seleccion < 1 || seleccion > opciones.size());
        return opciones.get(seleccion - 1);
    }
}