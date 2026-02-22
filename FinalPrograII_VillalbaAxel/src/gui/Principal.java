package gui;

import servicio.GestorVinos;
import modelo.Vino;
import persistencia.CsvVinosImportador;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class Principal extends javax.swing.JFrame {

    private final servicio.GestorVinos gestor;

    public Principal(GestorVinos gestor) {
        this.gestor = gestor;
        initComponents();
        configurarTabla();
    }

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
        if (v instanceof modelo.Tinto t) {
            return t.getUva().name();
        }
        if (v instanceof modelo.Blanco b) {
            return b.getUva().name();
        }
        if (v instanceof modelo.Rosado r) {
            return r.getUvaBase().name();
        }
        return "-";
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabelTitulo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnModificar = new javax.swing.JButton();
        btnBusqAvanzada = new javax.swing.JButton();
        btnCargarStock = new javax.swing.JButton();
        btnSerializar = new javax.swing.JButton();
        btnVerStock = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableVerStock = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabelTitulo.setFont(new java.awt.Font("Sitka Banner", 1, 48)); // NOI18N
        jLabelTitulo.setText("VINOTECA SOMMWINE");

        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/modificar.png"))); // NOI18N
        btnModificar.setText("Modificar");
        btnModificar.setToolTipText("");
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        btnBusqAvanzada.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/buscar.png"))); // NOI18N
        btnBusqAvanzada.setText("Busqueda Avanzada ");
        btnBusqAvanzada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBusqAvanzadaActionPerformed(evt);
            }
        });

        btnCargarStock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/uploadfile.png"))); // NOI18N
        btnCargarStock.setText("Cargar Stock");
        btnCargarStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarStockActionPerformed(evt);
            }
        });

        btnSerializar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/serializar.png"))); // NOI18N
        btnSerializar.setText("Crear Informes de Stock");
        btnSerializar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSerializarActionPerformed(evt);
            }
        });

        btnVerStock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/ver.png"))); // NOI18N
        btnVerStock.setText(" Ver Stock");
        btnVerStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerStockActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(109, 109, 109)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnBusqAvanzada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnVerStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnModificar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCargarStock, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                    .addComponent(btnSerializar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(116, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(btnVerStock)
                .addGap(18, 18, 18)
                .addComponent(btnBusqAvanzada)
                .addGap(18, 18, 18)
                .addComponent(btnModificar)
                .addGap(27, 27, 27)
                .addComponent(btnCargarStock)
                .addGap(18, 18, 18)
                .addComponent(btnSerializar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(81, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2, Short.MAX_VALUE)
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(147, 147, 147))
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
                        .addComponent(jLabel2)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(0, 21, Short.MAX_VALUE)))
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

    private void btnBusqAvanzadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBusqAvanzadaActionPerformed
        BusquedaAvanzada ba = new BusquedaAvanzada(gestor);
        ba.setLocationRelativeTo(this);
        ba.setVisible(true);
    }//GEN-LAST:event_btnBusqAvanzadaActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        Modificar m = new Modificar(gestor);
        m.setLocationRelativeTo(this);
        m.setVisible(true);
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnSerializarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSerializarActionPerformed
        if (gestor == null || gestor.listar().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, "No hay vinos para exportar.");
            return;
        }

        String[] opciones = {"Exportar CSV", "Exportar TXT", "Cancelar"};

        int op = javax.swing.JOptionPane.showOptionDialog(
                this,
                "¿Qué formato querés generar?",
                "Crear informe de stock",
                javax.swing.JOptionPane.DEFAULT_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (op == 2 || op == javax.swing.JOptionPane.CLOSED_OPTION) {
            return;
        }

        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setDialogTitle("Guardar archivo");

        // Nombre sugerido
        if (op == 0) {
            chooser.setSelectedFile(new java.io.File("stock_vinos.csv"));
        } else {
            chooser.setSelectedFile(new java.io.File("stock_vinos.txt"));
        }

        int resultado = chooser.showSaveDialog(this);
        if (resultado != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }

        java.io.File archivo = chooser.getSelectedFile();

        try {
            // Forzar extensión correcta si el usuario no la puso
            String ruta = archivo.getAbsolutePath();
            if (op == 0 && !ruta.toLowerCase().endsWith(".csv")) {
                archivo = new java.io.File(ruta + ".csv");
            } else if (op == 1 && !ruta.toLowerCase().endsWith(".txt")) {
                archivo = new java.io.File(ruta + ".txt");
            }

            if (op == 0) {
                persistencia.ExportadorVinos.exportarCSV(archivo, gestor.listar());
                javax.swing.JOptionPane.showMessageDialog(this, "CSV exportado correctamente:\n" + archivo.getAbsolutePath());
            } else {
                persistencia.ExportadorVinos.exportarTXT(archivo, gestor.listar());
                javax.swing.JOptionPane.showMessageDialog(this, "TXT exportado correctamente:\n" + archivo.getAbsolutePath());
            }

        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnSerializarActionPerformed

    private void btnVerStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerStockActionPerformed

        mostrarEnTabla(gestor.listar());

    }//GEN-LAST:event_btnVerStockActionPerformed

    private void btnCargarStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarStockActionPerformed
        String[] opciones = {"Por unidad", "Por archivo CSV", "Cancelar"};

        int op = javax.swing.JOptionPane.showOptionDialog(
                this,
                "¿Cómo querés cargar vinos al stock?",
                "Cargar Stock",
                javax.swing.JOptionPane.DEFAULT_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (op == 0) {
            // POR UNIDAD
            Editar alta = new Editar(gestor, () -> {
                // Si después querés refrescar la tabla del principal, acá lo llamás
                // refrescarTablaPrincipal();
            });
            alta.setLocationRelativeTo(this);
            alta.setVisible(true);

        } else if (op == 1) {
            // POR ARCHIVO CSV
            javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
            chooser.setDialogTitle("Seleccionar archivo CSV de vinos");

            int resultado = chooser.showOpenDialog(this);
            if (resultado == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File archivo = chooser.getSelectedFile();

                try {
                    CsvVinosImportador.ResultadoImportacion res = CsvVinosImportador.importar(archivo, gestor);

                    StringBuilder msg = new StringBuilder();
                    msg.append("Importación finalizada.\n");
                    msg.append("Cargados: ").append(res.getCargados()).append("\n");
                    msg.append("Errores: ").append(res.getErrores());

                    if (res.getErrores() > 0) {
                        msg.append("\n\nPrimeros errores:\n");
                        int limite = Math.min(5, res.getDetalleErrores().size());
                        for (int i = 0; i < limite; i++) {
                            msg.append("- ").append(res.getDetalleErrores().get(i)).append("\n");
                        }
                    }

                    javax.swing.JOptionPane.showMessageDialog(this, msg.toString());

                    // Si querés refrescar la tabla del principal cuando importa:
                    // mostrarEnTabla(gestor.listar());
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(
                            this,
                            "Error al importar archivo: " + ex.getMessage()
                    );
                }
            }
        }
        // Si cancela, no hace nada
    }//GEN-LAST:event_btnCargarStockActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBusqAvanzada;
    private javax.swing.JButton btnCargarStock;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnSerializar;
    private javax.swing.JButton btnVerStock;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabelTitulo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableVerStock;
    // End of variables declaration//GEN-END:variables
}
