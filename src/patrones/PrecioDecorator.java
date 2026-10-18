package patrones;

import modelo.Funcion;

/** Decorator: permite sumar reglas de precio sin modificar el calculador base. */
public abstract class PrecioDecorator implements PrecioStrategy {
    protected final PrecioStrategy componente;
    protected PrecioDecorator(PrecioStrategy componente) { this.componente = componente; }
}
