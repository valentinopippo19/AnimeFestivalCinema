package seguridad;

import java.util.Optional;

public interface CuentaRepository {
    void guardar(Cuenta cuenta);
    Optional<Cuenta> buscarPorEmail(String email);
}
