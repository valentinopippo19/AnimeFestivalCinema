package seguridad;

public class Cuenta {
    private final String nombre;
    private final String email;
    private final String passwordHash;
    private final String nacionalidad;

    public Cuenta(String nombre, String email, String passwordHash, String nacionalidad) {
        this.nombre = nombre;
        this.email = email;
        this.passwordHash = passwordHash;
        this.nacionalidad = nacionalidad;
    }

    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getNacionalidad() { return nacionalidad; }
}
