package patrones;

import modelo.Funcion;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DescuentoAnticipadoDecorator extends PrecioDecorator {
    private final int diasMinimos;
    private final int porcentaje;
    public DescuentoAnticipadoDecorator(PrecioStrategy componente, int diasMinimos, int porcentaje) {
        super(componente); this.diasMinimos = diasMinimos; this.porcentaje = porcentaje;
    }
    @Override
    public double calcular(Funcion funcion) {
        double precio = componente.calcular(funcion);
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), funcion.getFecha());
        if (dias >= diasMinimos) precio -= precio * porcentaje / 100.0;
        return precio;
    }
}
