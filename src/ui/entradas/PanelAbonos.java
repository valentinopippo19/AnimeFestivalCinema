package ui.entradas;

import modelo.Abono;
import modelo.TipoTarifa;
import ui.VentanaPrincipal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.Arrays;

/** Panel Swing para venta de abonos del festival anime. */
public class PanelAbonos extends JPanel {
    private final VentanaPrincipal ventana;
    private final DefaultTableModel modelo;

    public PanelAbonos(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titulo = new JLabel("Abonos Otaku");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(titulo, BorderLayout.NORTH);
        modelo = new DefaultTableModel(new Object[]{"Código", "Espectador", "Tipo", "Precio", "Cobertura"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        add(new JScrollPane(new JTable(modelo)), BorderLayout.CENTER);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton tres = new JButton("Comprar 3 días");
        tres.addActionListener(e -> comprarTresDias());
        JButton full = new JButton("Comprar Full");
        full.addActionListener(e -> comprarFull());
        JButton streaming = new JButton("Comprar Streaming");
        streaming.addActionListener(e -> comprarStreaming());
        botones.add(tres); botones.add(full); botones.add(streaming);
        add(botones, BorderLayout.SOUTH);
        cargarDatos();
    }

    private String seleccionarEspectador() {
        if (ventana.getEntradaServicio().getEspectadores().isEmpty()) throw new IllegalStateException("No hay espectadores registrados.");
        String[] nombres = ventana.getEntradaServicio().getEspectadores().stream().map(e -> e.getNombre()).toArray(String[]::new);
        return (String) JOptionPane.showInputDialog(this, "Espectador:", "Abono", JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
    }

    private TipoTarifa seleccionarTarifa() {
        return (TipoTarifa) JOptionPane.showInputDialog(this, "Tarifa:", "Abono", JOptionPane.PLAIN_MESSAGE, null,
                TipoTarifa.values(), TipoTarifa.GENERAL);
    }

    private void comprarTresDias() {
        try {
            String espectador = seleccionarEspectador(); if (espectador == null) return;
            TipoTarifa tarifa = seleccionarTarifa(); if (tarifa == null) return;
            String texto = JOptionPane.showInputDialog(this, "Ingrese exactamente 3 fechas con formato AAAA-MM-DD separadas por coma:");
            if (texto == null) return;
            var dias = Arrays.stream(texto.split(",")).map(String::trim).map(LocalDate::parse).toList();
            ventana.getEntradaServicio().comprarAbonoTresDias(espectador, tarifa, dias);
            cargarDatos();
            ventana.setEstado("Abono de 3 días vendido.");
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void comprarFull() {
        try {
            String espectador = seleccionarEspectador(); if (espectador == null) return;
            TipoTarifa tarifa = seleccionarTarifa(); if (tarifa == null) return;
            ventana.getEntradaServicio().comprarAbonoFull(espectador, tarifa);
            cargarDatos();
            ventana.setEstado("Abono Full vendido.");
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void comprarStreaming() {
        try {
            String espectador = seleccionarEspectador(); if (espectador == null) return;
            TipoTarifa tarifa = seleccionarTarifa(); if (tarifa == null) return;
            ventana.getEntradaServicio().comprarAbonoStreaming(espectador, tarifa);
            cargarDatos();
            ventana.setEstado("Abono Streaming vendido.");
        } catch (Exception ex) { mostrarError(ex); }
    }

    private void cargarDatos() {
        modelo.setRowCount(0);
        for (Abono a : ventana.getEntradaServicio().getAbonos()) {
            modelo.addRow(new Object[]{a.getCodigo(), a.getEspectador().getNombre(), a.getTipo(), a.getPrecio(), a.getCobertura()});
        }
    }
    private void mostrarError(Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Operación inválida", JOptionPane.ERROR_MESSAGE); }
}
