package servicios;

import modelo.*;
import excepciones.*;
import patrones.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class EntradaServicio {
    // Precios base (en pesos)
    public static final double PRECIO_ENTRADA_PRESENCIAL = 8000;
    public static final double PRECIO_ENTRADA_STREAMING = 5000;
    public static final double PRECIO_ABONO_TRES_DIAS = 20000;
    public static final double PRECIO_ABONO_FULL = 45000;
    public static final double PRECIO_ABONO_STREAMING = 25000;

    // Descuento por compra anticipada: si la función es dentro de 7 días o más
    public static final int DIAS_COMPRA_ANTICIPADA = 7;
    public static final int PORCENTAJE_COMPRA_ANTICIPADA = 10;

    private List<Funcion> funciones;
    private List<Espectador> espectadores;
    private List<Entrada> entradas;
    private List<Abono> abonos;
    private int contadorCodigo;
    private final EntradaFactory entradaFactory;
    private final AbonoFactory abonoFactory;
    private final ReservaEventBus eventos;
    private Festival festival;

    public EntradaServicio() {
        this.funciones = new ArrayList<>();
        this.espectadores = new ArrayList<>();
        this.entradas = new ArrayList<>();
        this.abonos = new ArrayList<>();
        this.contadorCodigo = 1;
        this.entradaFactory = new EntradaFactory();
        this.abonoFactory = new AbonoFactory();
        this.eventos = new ReservaEventBus();
        this.festival = null;
    }

    /** Vincula el servicio con el festival para validar edición y fechas de las funciones. */
    public void setFestival(Festival festival) { this.festival = festival; }

    public void registrarEspectador(String nombre, String email, String nacionalidad) {
        Espectador e = new Espectador(nombre, email, nacionalidad);
        espectadores.add(e);
    }

    public void programarFuncionPresencial(Pelicula pelicula, Sala sala, LocalDate fecha, LocalTime horario) {
        programarFuncionPresencial(pelicula, sala, fecha, horario, "Festival Online", "Mundo");
    }

    public void programarFuncionPresencial(Pelicula pelicula, Sala sala, LocalDate fecha, LocalTime horario, String ciudad, String pais) {
        validarFechaProgramacion(fecha, horario, pelicula);
        validarPeliculaEnEdicion(pelicula, fecha);
        for (Funcion f : funciones) {
            if (f instanceof FuncionPresencial fp
                    && fp.getSala().getNombre().equalsIgnoreCase(sala.getNombre())
                    && f.seSuperponeCon(new FuncionPresencial(fecha, horario, pelicula, sala))) {
                throw new FuncionNoDisponibleException("La sala ya tiene otra función que se superpone con este horario.");
            }
        }
        FuncionPresencial funcion = new FuncionPresencial(fecha, horario, pelicula, sala, ciudad, pais);
        pelicula.agregarFuncion(funcion);
        funciones.add(funcion);
    }

    public void programarFuncionStreaming(Pelicula pelicula, String enlace, int capacidad, LocalDate fecha, LocalTime horario) {
        programarFuncionStreaming(pelicula, enlace, capacidad, fecha, horario, "Festival Online", "Mundo");
    }

    public void programarFuncionStreaming(Pelicula pelicula, String enlace, int capacidad, LocalDate fecha, LocalTime horario, String ciudad, String pais) {
        validarFechaProgramacion(fecha, horario, pelicula);
        validarPeliculaEnEdicion(pelicula, fecha);
        if (capacidad <= 0) throw new FestivalException("La capacidad virtual debe ser mayor a 0.");
        FuncionStreaming funcion = new FuncionStreaming(fecha, horario, pelicula, enlace, capacidad, ciudad, pais);
        pelicula.agregarFuncion(funcion);
        funciones.add(funcion);
    }

    private void validarFechaProgramacion(LocalDate fecha, LocalTime horario, Pelicula pelicula) {
        if (fecha == null || horario == null) throw new FestivalException("La fecha y el horario son obligatorios.");
        if (LocalDateTime.of(fecha, horario).isBefore(LocalDateTime.now())) {
            throw new FestivalException("No se puede programar una función en una fecha y hora pasadas.");
        }
        if (pelicula == null) throw new FestivalException("La película es obligatoria.");
    }

    private void validarPeliculaEnEdicion(Pelicula pelicula, LocalDate fecha) {
        if (festival == null) return;
        boolean pertenece = false;
        boolean fechaValida = false;
        for (Edicion edicion : festival.getEdiciones()) {
            if (edicion.incluye(pelicula)) {
                pertenece = true;
                fechaValida |= edicion.contieneFecha(fecha);
            }
        }
        if (!pertenece) throw new FestivalException("La película debe competir en una edición antes de programar una función.");
        if (!fechaValida) throw new FestivalException("La fecha de la función debe estar dentro de la edición en la que compite la película.");
    }

    // ========== PRECIOS ==========

    public double calcularPrecioEntrada(Funcion funcion, TipoTarifa tarifa) {
        PrecioStrategy precio = new PrecioBaseStrategy();
        precio = new DescuentoTarifaDecorator(precio, tarifa);
        precio = new DescuentoAnticipadoDecorator(precio, DIAS_COMPRA_ANTICIPADA, PORCENTAJE_COMPRA_ANTICIPADA);
        return precio.calcular(funcion);
    }

    public double calcularPrecioAbono(String tipoAbono, TipoTarifa tarifa) {
        double precio = switch (tipoAbono) {
            case "TRES_DIAS" -> PRECIO_ABONO_TRES_DIAS;
            case "FULL" -> PRECIO_ABONO_FULL;
            case "STREAMING" -> PRECIO_ABONO_STREAMING;
            default -> throw new AbonoInvalidoException("tipo de abono desconocido: " + tipoAbono);
        };
        return precio - precio * tarifa.getPorcentajeDescuento() / 100.0;
    }

    // ========== VENTA DE ENTRADAS SUELTAS ==========

    public EntradaPresencial venderEntradaPresencial(String nombreEspectador, FuncionPresencial funcion, int numeroButaca) {
        return venderEntradaPresencial(nombreEspectador, funcion, numeroButaca, TipoTarifa.GENERAL);
    }

    public EntradaPresencial venderEntradaPresencial(String nombreEspectador, FuncionPresencial funcion, int numeroButaca, TipoTarifa tarifa) {
        Espectador espectador = buscarEspectador(nombreEspectador);
        double precio = calcularPrecioEntrada(funcion, tarifa);
        return crearEntradaPresencial(espectador, funcion, numeroButaca, precio, null);
    }

    public EntradaStreaming venderEntradaStreaming(String nombreEspectador, FuncionStreaming funcion) {
        return venderEntradaStreaming(nombreEspectador, funcion, TipoTarifa.GENERAL);
    }

    public EntradaStreaming venderEntradaStreaming(String nombreEspectador, FuncionStreaming funcion, TipoTarifa tarifa) {
        Espectador espectador = buscarEspectador(nombreEspectador);
        double precio = calcularPrecioEntrada(funcion, tarifa);
        return crearEntradaStreaming(espectador, funcion, precio, null);
    }

    private EntradaPresencial crearEntradaPresencial(Espectador espectador, FuncionPresencial funcion,
                                                     int numeroButaca, double precio, Abono abono) {
        if (numeroButaca < 1 || numeroButaca > funcion.getSala().getCantidadButacas()) {
            throw new FestivalException("Número de butaca inválido. Debe estar entre 1 y " + funcion.getSala().getCantidadButacas());
        }
        if (!funcion.puedeReservar()) {
            throw new CapacidadExcedidaException(funcion.getPelicula().getTitulo());
        }
        if (!funcion.butacaDisponible(numeroButaca)) {
            throw new ButacaOcupadaException(numeroButaca);
        }
        String codigo = "EP-" + contadorCodigo++;
        EntradaPresencial entrada = entradaFactory.crearPresencial(codigo, funcion, espectador, numeroButaca, precio, abono);
        funcion.ocuparButaca(numeroButaca);
        espectador.agregarEntrada(entrada);
        if (abono != null) abono.agregarReserva(entrada);
        entradas.add(entrada);
        eventos.publicar(new EventoReserva(espectador.getNombre(), funcion.getPelicula().getTitulo(),
                funcion, numeroButaca, entrada.getCodigo()));
        return entrada;
    }

    private EntradaStreaming crearEntradaStreaming(Espectador espectador, FuncionStreaming funcion,
                                                   double precio, Abono abono) {
        if (!funcion.puedeReservar()) {
            throw new CapacidadExcedidaException(funcion.getPelicula().getTitulo());
        }
        String codigo = "ES-" + contadorCodigo++;
        EntradaStreaming entrada = entradaFactory.crearStreaming(codigo, funcion, espectador, precio, abono);
        funcion.agregarEspectador();
        espectador.agregarEntrada(entrada);
        if (abono != null) abono.agregarReserva(entrada);
        entradas.add(entrada);
        return entrada;
    }

    // ========== ABONOS ==========

    public AbonoTresDias comprarAbonoTresDias(String nombreEspectador, TipoTarifa tarifa, List<LocalDate> dias) {
        if (dias.size() != 3) {
            throw new AbonoInvalidoException("debe elegir exactamente 3 días.");
        }
        List<LocalDate> fechasConFunciones = getFechasConFuncionesPresenciales();
        List<LocalDate> diasElegidos = new ArrayList<>();
        for (LocalDate d : dias) {
            if (diasElegidos.contains(d)) {
                throw new AbonoInvalidoException("los 3 días deben ser distintos.");
            }
            if (d.isBefore(LocalDate.now())) {
                throw new AbonoInvalidoException("el día " + d + " ya pasó.");
            }
            if (!fechasConFunciones.contains(d)) {
                throw new AbonoInvalidoException("no hay funciones presenciales el " + d + ".");
            }
            diasElegidos.add(d);
        }
        diasElegidos.sort(null);
        Espectador espectador = buscarEspectador(nombreEspectador);
        double precio = calcularPrecioAbono("TRES_DIAS", tarifa);
        AbonoTresDias abono = (AbonoTresDias) abonoFactory.crear("TRES_DIAS", "AB-" + contadorCodigo++, espectador, tarifa, precio, diasElegidos);
        registrarAbono(abono);
        return abono;
    }

    public AbonoFull comprarAbonoFull(String nombreEspectador, TipoTarifa tarifa) {
        Espectador espectador = buscarEspectador(nombreEspectador);
        double precio = calcularPrecioAbono("FULL", tarifa);
        AbonoFull abono = (AbonoFull) abonoFactory.crear("FULL", "AB-" + contadorCodigo++, espectador, tarifa, precio, null);
        registrarAbono(abono);
        return abono;
    }

    public AbonoStreaming comprarAbonoStreaming(String nombreEspectador, TipoTarifa tarifa) {
        Espectador espectador = buscarEspectador(nombreEspectador);
        double precio = calcularPrecioAbono("STREAMING", tarifa);
        AbonoStreaming abono = (AbonoStreaming) abonoFactory.crear("STREAMING", "AB-" + contadorCodigo++, espectador, tarifa, precio, null);
        registrarAbono(abono);
        return abono;
    }

    private void registrarAbono(Abono abono) {
        abono.getEspectador().agregarAbono(abono);
        abonos.add(abono);
    }

    public EntradaPresencial reservarPresencialConAbono(String codigoAbono, FuncionPresencial funcion, int numeroButaca) {
        Abono abono = buscarAbono(codigoAbono);
        validarReserva(abono, funcion);
        return crearEntradaPresencial(abono.getEspectador(), funcion, numeroButaca, 0, abono);
    }

    public EntradaStreaming reservarStreamingConAbono(String codigoAbono, FuncionStreaming funcion) {
        Abono abono = buscarAbono(codigoAbono);
        validarReserva(abono, funcion);
        return crearEntradaStreaming(abono.getEspectador(), funcion, 0, abono);
    }

    private void validarReserva(Abono abono, Funcion funcion) {
        if (funcion.getFecha().isBefore(LocalDate.now())) {
            throw new AbonoInvalidoException("la función ya pasó.");
        }
        if (!abono.cubre(funcion)) {
            throw new AbonoInvalidoException("el " + abono.getTipo() + " no cubre la función del " + funcion.getFecha() + ".");
        }
        if (abono.tieneReservaEn(funcion.getFecha(), funcion.getHorario())) {
            throw new AbonoInvalidoException("ya tiene una reserva el " + funcion.getFecha() + " a las " + funcion.getHorario() + ".");
        }
    }

    public void cancelarReserva(String codigoEntrada) {
        Entrada entrada = buscarEntrada(codigoEntrada);
        if (!entrada.esReservaDeAbono()) {
            throw new FestivalException("Solo se pueden cancelar reservas hechas con abono.");
        }
        if (entrada.getFuncion().getFecha().isBefore(LocalDate.now())) {
            throw new FestivalException("No se puede cancelar una reserva de una función que ya pasó.");
        }
        if (entrada instanceof EntradaPresencial) {
            FuncionPresencial fp = (FuncionPresencial) entrada.getFuncion();
            fp.liberarButaca(((EntradaPresencial) entrada).getButaca());
        } else {
            ((FuncionStreaming) entrada.getFuncion()).quitarEspectador();
        }
        entrada.getAbono().quitarReserva(entrada);
        entrada.getEspectador().quitarEntrada(entrada);
        entradas.remove(entrada);
    }

    // Fechas (sin repetir y ordenadas) en las que hay funciones presenciales
    public List<LocalDate> getFechasConFuncionesPresenciales() {
        List<LocalDate> fechas = new ArrayList<>();
        for (Funcion f : funciones) {
            if (f instanceof FuncionPresencial && !fechas.contains(f.getFecha())) {
                fechas.add(f.getFecha());
            }
        }
        fechas.sort(null);
        return fechas;
    }

    // ========== BÚSQUEDAS ==========

    public Espectador buscarEspectador(String nombre) {
        for (Espectador e : espectadores) {
            if (e.getNombre().equalsIgnoreCase(nombre)) return e;
        }
        throw new EntidadNoEncontradaException("Espectador", "nombre: " + nombre);
    }

    public Funcion buscarFuncion(String tituloPelicula, LocalDate fecha) {
        for (Funcion f : funciones) {
            if (f.getPelicula().getTitulo().equalsIgnoreCase(tituloPelicula) && f.getFecha().equals(fecha)) {
                return f;
            }
        }
        throw new EntidadNoEncontradaException("Funcion", "pelicula: " + tituloPelicula);
    }

    public Abono buscarAbono(String codigo) {
        for (Abono a : abonos) {
            if (a.getCodigo().equalsIgnoreCase(codigo)) return a;
        }
        throw new EntidadNoEncontradaException("Abono", "codigo: " + codigo);
    }

    public Entrada buscarEntrada(String codigo) {
        for (Entrada e : entradas) {
            if (e.getCodigo().equalsIgnoreCase(codigo)) return e;
        }
        throw new EntidadNoEncontradaException("Entrada", "codigo: " + codigo);
    }

    public List<Funcion> getFunciones() {
        return funciones;
    }
    public List<Espectador> getEspectadores() {
        return espectadores;
    }
    public List<Entrada> getEntradas() {
        return entradas;
    }
    public List<Abono> getAbonos() {
        return abonos;
    }

    public int getContadorCodigo() { return contadorCodigo; }

    public void restaurar(List<Espectador> espectadores, List<Funcion> funciones, List<Entrada> entradas,
                          List<Abono> abonos, int contadorCodigo) {
        this.espectadores = espectadores == null ? new ArrayList<>() : espectadores;
        this.funciones = funciones == null ? new ArrayList<>() : funciones;
        this.entradas = entradas == null ? new ArrayList<>() : entradas;
        this.abonos = abonos == null ? new ArrayList<>() : abonos;
        this.contadorCodigo = Math.max(1, contadorCodigo);
    }

    public ReservaEventBus getEventos() {
        return eventos;
    }
}
