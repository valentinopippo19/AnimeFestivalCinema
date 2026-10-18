package ui.festival;

import modelo.Edicion;
import modelo.Premio;
import modelo.Seccion;
import modelo.TipoSeccion;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Panel Swing para administrar las secciones y sus premios. */
public class PanelSecciones extends JPanel {
    private final VentanaPrincipal ventana;
    private final DefaultTableModel modelo;

    public PanelSecciones(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Secciones y Premios");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new Object[]{"Edición", "Sección", "Premio"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        add(new JScrollPane(new JTable(modelo)), BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton agregar = new JButton("Agregar sección");
        agregar.addActionListener(e -> agregarSeccion());
        JButton actualizar = new JButton("Actualizar");
        actualizar.addActionListener(e -> cargarDatos());
        botones.add(agregar);
        botones.add(actualizar);
        add(botones, BorderLayout.SOUTH);
        cargarDatos();
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        for (Edicion edicion : ventana.getFestivalServicio().getEdiciones()) {
            for (Seccion seccion : edicion.getSecciones()) {
                String premio = seccion.getPremio() == null ? "Sin premio" : seccion.getPremio().getNombre();
                modelo.addRow(new Object[]{edicion.getAnio(), seccion.getTipo(), premio});
            }
        }
    }

    private void agregarSeccion() {
        try {
            if (ventana.getFestivalServicio().getEdiciones().isEmpty()) {
                throw new IllegalStateException("Primero debe existir una edición.");
            }
            String[] anios = ventana.getFestivalServicio().getEdiciones().stream()
                    .map(e -> String.valueOf(e.getAnio())).toArray(String[]::new);
            String anio = (String) JOptionPane.showInputDialog(this, "Edición:", "Nueva sección",
                    JOptionPane.PLAIN_MESSAGE, null, anios, anios[0]);
            if (anio == null) return;
            TipoSeccion tipo = (TipoSeccion) JOptionPane.showInputDialog(this, "Tipo de sección:", "Nueva sección",
                    JOptionPane.PLAIN_MESSAGE, null, TipoSeccion.values(), TipoSeccion.values()[0]);
            if (tipo == null) return;
            String nombrePremio = JOptionPane.showInputDialog(this, "Nombre del premio:", "Premio de la sección");
            if (nombrePremio == null) return;
            String descripcion = JOptionPane.showInputDialog(this, "Descripción del premio:", "Premio de la sección");
            if (descripcion == null) return;
            ventana.getFestivalServicio().registrarSeccion(Integer.parseInt(anio), tipo,
                    new Premio(nombrePremio, descripcion));
            cargarDatos();
            ventana.setEstado("Sección registrada correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo registrar", JOptionPane.ERROR_MESSAGE);
        }
    }
}
