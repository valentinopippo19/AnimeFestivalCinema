package modelo;

import java.io.Serializable;

public abstract class Persona implements Serializable {
    private String nombre;
    private String email;
    private String nacionalidad;

    public Persona(String nombre, String email, String nacionalidad) {
        if (nombre == null || nombre.isBlank()) throw new excepciones.FestivalException("El nombre es obligatorio.");
        if (email == null || email.isBlank()) throw new excepciones.FestivalException("El email es obligatorio.");
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new excepciones.FestivalException("El email no tiene un formato válido.");
        }
        if (nacionalidad == null || nacionalidad.isBlank()) throw new excepciones.FestivalException("La nacionalidad es obligatoria.");
        this.nombre = nombre.trim();
        this.email = email.trim().toLowerCase();
        this.nacionalidad = nacionalidad.trim();
    }

    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getNacionalidad() { return nacionalidad; }

    @Override
    public String toString() {
        return nombre + " (" + nacionalidad + ")";
    }
}