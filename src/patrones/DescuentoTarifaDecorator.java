package patrones;

import modelo.Funcion;
import modelo.TipoTarifa;

public class DescuentoTarifaDecorator extends PrecioDecorator {
    private final TipoTarifa tarifa;
    public DescuentoTarifaDecorator(PrecioStrategy componente, TipoTarifa tarifa) {
        super(componente); this.tarifa = tarifa;
    }
    @Override
    public double calcular(Funcion funcion) {
        double precio = componente.calcular(funcion);
        return precio - precio * tarifa.getPorcentajeDescuento() / 100.0;
    }
}
