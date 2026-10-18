package modelo;

import java.io.Serializable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FuncionPresencial extends Funcion implements Serializable {
    private Sala sala;
    private List<Integer> butacasOcupadas;

    public FuncionPresencial(LocalDate fecha, LocalTime horario, Pelicula pelicula, Sala sala) {
        super(fecha, horario, pelicula);
        this.sala = sala;
        this.butacasOcupadas = new ArrayList<>();
    }

    public FuncionPresencial(LocalDate fecha, LocalTime horario, Pelicula pelicula, Sala sala, String ciudad, String pais) {
        super(fecha, horario, pelicula, ciudad, pais);
        this.sala = sala;
        this.butacasOcupadas = new ArrayList<>();
    }

    public void liberarButaca(int numeroButaca) { butacasOcupadas.remove(Integer.valueOf(numeroButaca)); }

    @Override
    public boolean verificarDisponibilidad() {
        return butacasOcupadas.size() < sala.getCantidadButacas();
    }

    public void ocuparButaca(int numeroButaca) {
        butacasOcupadas.add(numeroButaca);
    }

    public boolean butacaDisponible(int numeroButaca) {
        return !butacasOcupadas.contains(numeroButaca);
    }


    @Override
    public double calcularOcupacion() {
        return (double) butacasOcupadas.size() / sala.getCantidadButacas() * 100;
    }

    public Sala getSala() {
        return sala;
    }
    public List<Integer> getButacasOcupadas() {
        return butacasOcupadas;
    }


    @Override
    public String toString() {
        return "Presencial - " + super.toString() + " - Sala: " + sala.getNombre();
    }
}
