package patrones;

import modelo.FuncionPresencial;

public record EventoReserva(String usuario, String pelicula, FuncionPresencial funcion, int butaca, String codigo) {}
