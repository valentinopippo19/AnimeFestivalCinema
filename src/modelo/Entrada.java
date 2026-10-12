package modelo;

public abstract class Entrada {
    private String codigo;
    private Funcion funcion;
    private Espectador espectador;
    private double precio;
    private Abono abono;

    public Entrada(String codigo, Funcion funcion, Espectador espectador) {
        this(codigo, funcion, espectador, 0, null);
    }

    public Entrada(String codigo, Funcion funcion, Espectador espectador, double precio, Abono abono) {
        this.codigo = codigo;
        this.funcion = funcion;
        this.espectador = espectador;
        this.precio = precio;
        this.abono = abono;
    }

    public String getCodigo() {
        return codigo;
    }
    public Funcion getFuncion() {
        return funcion;
    }
    public Espectador getEspectador() {
        return espectador;
    }

    public double getPrecio() { return precio; }
    public Abono getAbono() { return abono; }
    public boolean esReservaDeAbono() { return abono != null; }

    @Override
    public String toString() {
        return "Entrada: " + codigo + " - " + funcion.getPelicula().getTitulo()
                + " - " + funcion.getFecha() + " " + funcion.getHorario();
    }
}