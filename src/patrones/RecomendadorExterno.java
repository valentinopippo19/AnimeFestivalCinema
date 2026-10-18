package patrones;

import java.util.List;

/** API incompatible hipotética de un proveedor externo de recomendaciones. */
public class RecomendadorExterno {
    public List<String> obtenerTitulos(String genero, int limite) {
        return List.of("Your Name", "Suzume", "Akira").subList(0, Math.min(limite, 3));
    }
}
