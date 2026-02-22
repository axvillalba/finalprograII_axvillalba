package gui;

import modelo.*;
import servicio.GestorVinos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;

public class BusquedaAvanzada extends javax.swing.JFrame {

    private final GestorVinos gestor;

    private static final String TODOS = "TODOS";
    private static final String TODAS = "TODAS";

    public BusquedaAvanzada(GestorVinos gestor) {
        this.gestor = gestor;

        initComponents();

        configurarTabla();
        inicializarCombos();
        mostrarEnTabla(gestor.listar());

        // Listeners
        cmbTipoVino.addActionListener(e -> recargarUvasSegunTipo());

        jButton1.addActionListener(e -> buscarYMostrar()); // Buscar
        jButton2.addActionListener(e -> limpiar());        // Limpiar
        jButton3.addActionListener(e -> volver());         // Volver
    }

    // =========================
    // TABLA
    // =========================
    private void configurarTabla() {
        DefaultTableModel modelo = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Tipo", "Nombre", "Bodega", "Año", "Uva", "Stock", "Precio"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        jTableVerStock.setModel(modelo);
    }

    private void mostrarEnTabla(List<Vino> lista) {
        DefaultTableModel modelo = (DefaultTableModel) jTableVerStock.getModel();
        modelo.setRowCount(0); // limpiar

        for (Vino v : lista) {
            modelo.addRow(new Object[]{
                v.getId(),
                v.getClass().getSimpleName(),
                v.getNombre(),
                v.getBodega(),
                v.getAnioProduccion(),
                obtenerUva(v),
                v.getStockBotellas(),
                v.getPrecio()
            });
        }
    }

    private String obtenerUva(Vino v) {
        if (v instanceof Tinto t) {
            return t.getUva().name();
        }
        if (v instanceof Blanco b) {
            return b.getUva().name();
        }
        if (v instanceof Rosado r) {
            return r.getUvaBase().name();
        }
        return "-";
    }

    // =========================
    // COMBOS
    // =========================
    private void inicializarCombos() {
        // Tipo de vino
        cmbTipoVino.removeAllItems();
        cmbTipoVino.addItem(TODOS);
        cmbTipoVino.addItem("TINTO");
        cmbTipoVino.addItem("BLANCO");
        cmbTipoVino.addItem("ROSADO");

        // Bodegas únicas
        cmbBodoga.removeAllItems();
        cmbBodoga.addItem(TODAS);
        for (String b : obtenerBodegasUnicas()) {
            cmbBodoga.addItem(b);
        }

        // Uva: depende del tipo
        cmbUva.removeAllItems();
        cmbUva.addItem(TODAS);
        cmbUva.setEnabled(false);

        // Nombre
        jTextField1.setText("");
    }

    private List<String> obtenerBodegasUnicas() {
        Set<String> set = new TreeSet<>(); // ordenado + sin repetidos
        for (Vino v : gestor.listar()) {
            if (v.getBodega() != null && !v.getBodega().isBlank()) {
                set.add(v.getBodega().trim());
            }
        }
        return new ArrayList<>(set);
    }

    private void recargarUvasSegunTipo() {
        String tipo = (String) cmbTipoVino.getSelectedItem();

        cmbUva.removeAllItems();
        cmbUva.addItem(TODAS);

        if (tipo == null || tipo.equals(TODOS)) {
            cmbUva.setEnabled(false);
            return;
        }

        cmbUva.setEnabled(true);

        switch (tipo) {
            case "TINTO" -> {
                for (UvaTinta u : UvaTinta.values()) {
                    cmbUva.addItem(u.name());
                }
            }
            case "BLANCO" -> {
                for (UvaBlanca u : UvaBlanca.values()) {
                    cmbUva.addItem(u.name());
                }
            }
            case "ROSADO" -> {
                // Rosado opción A: uva base tinta
                for (UvaTinta u : UvaTinta.values()) {
                    cmbUva.addItem(u.name());
                }
            }
        }
    }

    // =========================
    // FILTRO / BÚSQUEDA
    // =========================
    private void buscarYMostrar() {
        String tipo = safe((String) cmbTipoVino.getSelectedItem(), TODOS);
        String bodega = safe((String) cmbBodoga.getSelectedItem(), TODAS);
        String uva = safe((String) cmbUva.getSelectedItem(), TODAS);
        String nombre = jTextField1.getText() == null ? "" : jTextField1.getText().trim();

        List<Vino> res = new ArrayList<>(gestor.listar());

        // 1) Tipo
        if (!tipo.equals(TODOS)) {
            res.removeIf(v -> switch (tipo) {
                case "TINTO" ->
                    !(v instanceof Tinto);
                case "BLANCO" ->
                    !(v instanceof Blanco);
                case "ROSADO" ->
                    !(v instanceof Rosado);
                default ->
                    false;
            });
        }

        // 2) Bodega
        if (!bodega.equals(TODAS)) {
            String b = bodega.trim().toLowerCase();
            res.removeIf(v -> v.getBodega() == null || !v.getBodega().trim().toLowerCase().equals(b));
        }

        // 3) Uva (solo si tipo != TODOS y uva != TODAS)
        if (!tipo.equals(TODOS) && !uva.equals(TODAS)) {
            res.removeIf(v -> {
                if (v instanceof Tinto t) {
                    return !t.getUva().name().equals(uva);
                }
                if (v instanceof Blanco b) {
                    return !b.getUva().name().equals(uva);
                }
                if (v instanceof Rosado r) {
                    return !r.getUvaBase().name().equals(uva);
                }
                return true;
            });
        }

        // 4) Nombre contiene
        if (!nombre.isBlank()) {
            String n = nombre.toLowerCase();
            res.removeIf(v -> v.getNombre() == null || !v.getNombre().toLowerCase().contains(n));
        }

        mostrarEnTabla(res);
        JOptionPane.showMessageDialog(this, "Resultados: " + res.size());
    }

    private void limpiar() {
        cmbTipoVino.setSelectedItem(TODOS);
        cmbBodoga.setSelectedItem(TODAS);

        cmbUva.removeAllItems();
        cmbUva.addItem(TODAS);
        cmbUva.setEnabled(false);

        jTextField1.setText("");
        mostrarEnTabla(gestor.listar());
    }

    private void volver() {
        dispose();
    }

    private String safe(String v, String def) {
        return v == null ? def : v;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabelTitulo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        cmbTipoVino = new javax.swing.JComboBox<>();
        cmbUva = new javax.swing.JComboBox<>();
        cmbBodoga = new javax.swing.JComboBox<>();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableVerStock = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabelTitulo.setFont(new java.awt.Font("Sitka Banner", 1, 48)); // NOI18N
        jLabelTitulo.setText("VINOTECA SOMMWINE");

        cmbTipoVino.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbTipoVino.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbUva.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbUva.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbBodoga.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        cmbBodoga.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextField1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jTextField1.setText("txtfieldBuscarNombre");

        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/buscar.png"))); // NOI18N
        jButton1.setText("Buscar");

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/limpiar.png"))); // NOI18N
        jButton2.setText("Limpiar");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Tipo de Vino");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Bodega");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setText("Uva");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Buscar por nombre");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cmbUva, 0, 283, Short.MAX_VALUE)
                            .addComponent(cmbBodoga, 0, 283, Short.MAX_VALUE)
                            .addComponent(cmbTipoVino, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextField1)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(190, 190, 190)
                        .addComponent(jLabel4))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(181, 181, 181)
                        .addComponent(jLabel3))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(166, 166, 166)
                        .addComponent(jLabel1))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addComponent(jLabel5)))
                .addContainerGap(88, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbTipoVino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addGap(4, 4, 4)
                .addComponent(cmbBodoga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbUva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jTableVerStock.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTableVerStock);

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/logo2.png"))); // NOI18N

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/atras.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(147, 147, 147))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jLabelTitulo))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton3)
                                .addGap(14, 14, 14)))))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cmbBodoga;
    private javax.swing.JComboBox<String> cmbTipoVino;
    private javax.swing.JComboBox<String> cmbUva;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableVerStock;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
