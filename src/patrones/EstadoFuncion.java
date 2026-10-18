package patrones;

import modelo.Funcion;

/** State: cambia el comportamiento de una función según su estado del ciclo de vida. */
public interface EstadoFuncion {
    boolean puedeReservar(Funcion funcion);
    String nombre();
}
