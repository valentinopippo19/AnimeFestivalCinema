package patrones;

import modelo.*;
import java.time.LocalDate;
import java.util.List;

/** Factory: concentra la creación de los tipos de abono. */
public class AbonoFactory {
    public Abono crear(String tipo, String codigo, Espectador espectador, TipoTarifa tarifa,
                       double precio, List<LocalDate> dias) {
        return switch (tipo.toUpperCase()) {
            case "TRES_DIAS" -> new AbonoTresDias(codigo, espectador, tarifa, precio, dias);
            case "FULL" -> new AbonoFull(codigo, espectador, tarifa, precio);
            case "STREAMING" -> new AbonoStreaming(codigo, espectador, tarifa, precio);
            default -> throw new IllegalArgumentException("Tipo de abono desconocido: " + tipo);
        };
    }
}
