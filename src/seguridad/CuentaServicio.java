package seguridad;

import excepciones.FestivalException;
import modelo.Espectador;
import servicios.EntradaServicio;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** Facade + Service Layer: expone las operaciones de cuenta a la web. */
public class CuentaServicio {
    private final CuentaRepository repository;
    private final EntradaServicio entradaServicio;

    public CuentaServicio(CuentaRepository repository, EntradaServicio entradaServicio) {
        this.repository = repository;
        this.entradaServicio = entradaServicio;
    }

    public Cuenta registrar(String nombre, String email, String password, String nacionalidad) {
        if (nombre == null || nombre.isBlank() || email == null || email.isBlank() || password == null || password.length() < 6) {
            throw new FestivalException("Nombre, email y contraseña de al menos 6 caracteres son obligatorios.");
        }
        if (repository.buscarPorEmail(email).isPresent()) {
            throw new FestivalException("Ya existe una cuenta con ese email.");
        }
        Cuenta cuenta = new Cuenta(nombre.trim(), email.trim().toLowerCase(), hash(password), nacionalidad == null ? "Internacional" : nacionalidad.trim());
        repository.guardar(cuenta);
        try {
            entradaServicio.registrarEspectador(cuenta.getNombre(), cuenta.getEmail(), cuenta.getNacionalidad());
        } catch (RuntimeException ignored) {
            // Si el espectador ya existe, la cuenta sigue siendo válida.
        }
        return cuenta;
    }

    public Cuenta autenticar(String email, String password) {
        Cuenta cuenta = repository.buscarPorEmail(email).orElseThrow(() -> new FestivalException("Credenciales inválidas."));
        if (!cuenta.getPasswordHash().equals(hash(password))) throw new FestivalException("Credenciales inválidas.");
        return cuenta;
    }

    public String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
