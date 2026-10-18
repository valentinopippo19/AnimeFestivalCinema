package patrones;

import modelo.Funcion;
import java.io.Serializable;

/** State concreto: la función acepta reservas. */
public class FuncionDisponible implements EstadoFuncion, Serializable {
    @Override
    public boolean puedeReservar(Funcion funcion) {
        return !funcion.yaFinalizo() && funcion.verificarDisponibilidad();
    }
    @Override public String nombre() { return "DISPONIBLE"; }
}
