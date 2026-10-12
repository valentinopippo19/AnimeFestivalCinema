package patrones;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Observer Pattern: permite auditar/notificar reservas sin acoplar la lógica de venta. */
public class ReservaEventBus {
    private final List<ReservaObserver> observers = new CopyOnWriteArrayList<>();
    public void suscribir(ReservaObserver observer) { observers.add(observer); }
    public void publicar(EventoReserva evento) { observers.forEach(o -> o.actualizar(evento)); }
}
