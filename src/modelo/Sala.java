package modelo;

import java.io.Serializable;

public class Sala implements Serializable {
    private String nombre;
    private int cantidadButacas;

    public Sala(String nombre, int cantidadButacas) {
        if (nombre == null || nombre.isBlank()) throw new excepciones.FestivalException("El nombre de la sala es obligatorio.");
        if (cantidadButacas <= 0) throw new excepciones.FestivalException("La capacidad de la sala debe ser mayor a 0.");
        this.nombre = nombre.trim();
        this.cantidadButacas = cantidadButacas;
    }

    public boolean esButacaValida(int numero) {
        return numero >= 1 && numero <= cantidadButacas;
    }

    public String getNombre() {
        return nombre;
    }
    public int getCantidadButacas() {
        return cantidadButacas;
    }

    @Override
    public String toString() {
        return "Sala: " + nombre + " (capacidad: " + cantidadButacas + ")";
    }
}