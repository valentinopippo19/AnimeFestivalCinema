package patrones;

import modelo.Funcion;
import java.io.Serializable;

/** State concreto: la función ya pasó y no admite nuevas reservas. */
public class FuncionFinalizada implements EstadoFuncion, Serializable {
    @Override public boolean puedeReservar(Funcion funcion) { return false; }
    @Override public String nombre() { return "FINALIZADA"; }
}
