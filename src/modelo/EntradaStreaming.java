package modelo;

public class EntradaStreaming extends Entrada {
    private String enlaceAcceso;

    public EntradaStreaming(String codigo, Funcion funcion, Espectador espectador, String enlaceAcceso, double precio, Abono abono) {
        super(codigo, funcion, espectador, precio, abono);
        this.enlaceAcceso = enlaceAcceso;
    }

    public EntradaStreaming(String codigo, Funcion funcion, Espectador espectador, String enlaceAcceso) {
        this(codigo, funcion, espectador, enlaceAcceso, 0, null);
    }

    public String getEnlaceAcceso() { return enlaceAcceso; }

    @Override
    public String toString() {
        return super.toString() + " - Enlace: " + enlaceAcceso;
    }
}