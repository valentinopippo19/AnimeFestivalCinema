package patrones;

import modelo.*;

import java.util.Objects;

/** Factory: centraliza la creación de los distintos tipos de entrada. */
public class EntradaFactory {
    public EntradaPresencial crearPresencial(String codigo, FuncionPresencial funcion, Espectador espectador,
                                             int butaca, double precio, Abono abono) {
        Objects.requireNonNull(funcion); Objects.requireNonNull(espectador);
        return new EntradaPresencial(codigo, funcion, espectador, butaca, precio, abono);
    }

    public EntradaStreaming crearStreaming(String codigo, FuncionStreaming funcion, Espectador espectador,
                                            double precio, Abono abono) {
        Objects.requireNonNull(funcion); Objects.requireNonNull(espectador);
        return new EntradaStreaming(codigo, funcion, espectador, funcion.getEnlace(), precio, abono);
    }
}
