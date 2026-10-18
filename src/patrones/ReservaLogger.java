package patrones;

public class ReservaLogger implements ReservaObserver {
    @Override
    public void actualizar(EventoReserva evento) {
        System.out.println("[RESERVA] " + evento.usuario() + " -> " + evento.pelicula() + " / " + evento.funcion().getFecha() + " / butaca " + evento.butaca() + " / " + evento.codigo());
    }
}
