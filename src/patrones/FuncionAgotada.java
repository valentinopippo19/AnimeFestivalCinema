package patrones;

import modelo.Funcion;
import java.io.Serializable;

/** State concreto: se alcanzó la capacidad. */
public class FuncionAgotada implements EstadoFuncion, Serializable {
    @Override public boolean puedeReservar(Funcion funcion) { return false; }
    @Override public String nombre() { return "AGOTADA"; }
}
