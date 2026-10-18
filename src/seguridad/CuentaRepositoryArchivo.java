package seguridad;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** Repository persistente de cuentas. Guarda el hash, nunca la contraseña en texto plano. */
public class CuentaRepositoryArchivo implements CuentaRepository {
    private final File archivo;
    private final Map<String, Cuenta> cuentas = new HashMap<>();

    public CuentaRepositoryArchivo(String ruta) {
        this.archivo = new File(ruta);
        cargar();
    }

    @Override
    public synchronized void guardar(Cuenta cuenta) {
        cuentas.put(cuenta.getEmail().toLowerCase(), cuenta);
        persistir();
    }

    @Override
    public synchronized Optional<Cuenta> buscarPorEmail(String email) {
        return Optional.ofNullable(cuentas.get(email.toLowerCase()));
    }

    private void cargar() {
        if (!archivo.isFile()) return;
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(archivo)))) {
            Object value = in.readObject();
            if (value instanceof Map<?, ?> map) {
                for (Object entryObject : map.entrySet()) {
                    Map.Entry<?, ?> entry = (Map.Entry<?, ?>) entryObject;
                    if (entry.getKey() instanceof String key && entry.getValue() instanceof Cuenta cuenta) {
                        cuentas.put(key, cuenta);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Advertencia: no se pudieron cargar las cuentas persistidas: " + e.getMessage());
        }
    }

    private void persistir() {
        File temporal = new File(archivo.getPath() + ".tmp");
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(temporal)))) {
            out.writeObject(cuentas);
            if (!temporal.renameTo(archivo)) {
                java.nio.file.Files.move(temporal.toPath(), archivo.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            temporal.delete();
            throw new IllegalStateException("No se pudieron guardar las cuentas.", e);
        }
    }
}
