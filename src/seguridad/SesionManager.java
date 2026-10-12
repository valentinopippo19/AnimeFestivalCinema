package seguridad;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Singleton + Session Manager. Centraliza las sesiones activas del festival. */
public final class SesionManager {
    private static final SesionManager INSTANCE = new SesionManager();
    private final Map<String, Cuenta> sesiones = new ConcurrentHashMap<>();

    private SesionManager() {}
    public static SesionManager getInstance() { return INSTANCE; }

    public String iniciar(Cuenta cuenta) {
        String token = UUID.randomUUID().toString();
        sesiones.put(token, cuenta);
        return token;
    }

    public Cuenta obtener(String token) { return token == null ? null : sesiones.get(token); }
    public void cerrar(String token) { if (token != null) sesiones.remove(token); }
}
