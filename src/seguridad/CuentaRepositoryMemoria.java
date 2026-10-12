package seguridad;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Repository Pattern: desacopla la persistencia de cuentas del servicio. */
public class CuentaRepositoryMemoria implements CuentaRepository {
    private final Map<String, Cuenta> cuentas = new ConcurrentHashMap<>();

    @Override
    public void guardar(Cuenta cuenta) {
        cuentas.put(cuenta.getEmail().toLowerCase(), cuenta);
    }

    @Override
    public Optional<Cuenta> buscarPorEmail(String email) {
        return Optional.ofNullable(cuentas.get(email.toLowerCase()));
    }
}
