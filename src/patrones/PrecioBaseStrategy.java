package patrones;

import modelo.Funcion;
import modelo.FuncionPresencial;

/** Strategy concreta con los precios base del festival. */
public class PrecioBaseStrategy implements PrecioStrategy {
    public static final double PRESENCIAL = 8000;
    public static final double STREAMING = 5000;

    @Override
    public double calcular(Funcion funcion) {
        return funcion instanceof FuncionPresencial ? PRESENCIAL : STREAMING;
    }
}
