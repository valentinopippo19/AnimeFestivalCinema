package modelo;

import java.io.Serializable;

public class Premio implements Serializable {
    private String nombre;
    private String descripcion;

    public Premio(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }
    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return nombre + " - " + descripcion;
    }
}