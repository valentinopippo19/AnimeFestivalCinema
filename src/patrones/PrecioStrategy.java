package patrones;

import modelo.Funcion;

/** Strategy: cada política de precio calcula un precio base de forma intercambiable. */
public interface PrecioStrategy {
    double calcular(Funcion funcion);
}
