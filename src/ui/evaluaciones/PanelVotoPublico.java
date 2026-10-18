package ui.evaluaciones;

import modelo.Espectador;
import modelo.Pelicula;
import ui.VentanaPrincipal;

import javax.swing.*;
import java.awt.*;

/** Panel solicitado por el TP para el premio/voto del público. */
public class PanelVotoPublico extends JPanel {
    private final VentanaPrincipal ventana;

    public PanelVotoPublico(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        JLabel titulo = new JLabel("Premio del Público - Voto 1 a 5 estrellas");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(titulo, c);

        JComboBox<String> espectadores = new JComboBox<>();
        for (Espectador e : ventana.getEntradaServicio().getEspectadores()) espectadores.addItem(e.getNombre());
        JComboBox<String> peliculas = new JComboBox<>();
        for (Pelicula p : ventana.getPeliculaServicio().getPeliculas()) peliculas.addItem(p.getTitulo());
        JSpinner puntaje = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        JButton votar = new JButton("Registrar voto");

        c.gridwidth = 1; c.gridx = 0; c.gridy++;
        add(new JLabel("Espectador:"), c); c.gridx = 1; add(espectadores, c);
        c.gridx = 0; c.gridy++; add(new JLabel("Película:"), c); c.gridx = 1; add(peliculas, c);
        c.gridx = 0; c.gridy++; add(new JLabel("Estrellas:"), c); c.gridx = 1; add(puntaje, c);
        c.gridx = 0; c.gridy++; c.gridwidth = 2; add(votar, c);

        votar.addActionListener(e -> {
            try {
                if (espectadores.getSelectedItem() == null || peliculas.getSelectedItem() == null) {
                    throw new IllegalStateException("Debe seleccionar espectador y película.");
                }
                Espectador espectador = ventana.getEntradaServicio().buscarEspectador((String) espectadores.getSelectedItem());
                ventana.getPeliculaServicio().registrarVotoPublico(espectador,
                        (String) peliculas.getSelectedItem(), (Integer) puntaje.getValue());
                JOptionPane.showMessageDialog(this, "Voto registrado correctamente.", "Premio del Público", JOptionPane.INFORMATION_MESSAGE);
                ventana.setEstado("Voto público registrado.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Voto inválido", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
