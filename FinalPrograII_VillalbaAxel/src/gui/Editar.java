package gui;

import modelo.*;
import servicio.GestorVinos;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Editar extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Editar.class.getName());
    private final GestorVinos gestor;
    private final Vino vinoOriginal;
    private final Runnable alGuardar;
    private final boolean modoAlta;

    public Editar() {
        this.gestor = null;
        this.vinoOriginal = null;
        this.alGuardar = null;
        this.modoAlta = false;
        initComponents();
    }

// EDITAR vino existente
    public Editar(servicio.GestorVinos gestor, modelo.Vino vino, Runnable alGuardar) {
        this.gestor = gestor;
        this.vinoOriginal = vino;
        this.alGuardar = alGuardar;
        this.modoAlta = false;

        initComponents();
        inicializarCombos();
        cargarDatosEnPantalla();
        configurarEventos();
    }

// ALTA de vino nuevo
    public Editar(servicio.GestorVinos gestor, Runnable alGuardar) {
        this.gestor = gestor;
        this.vinoOriginal = null;
        this.alGuardar = alGuardar;
        this.modoAlta = true;

        initComponents();
        inicializarCombos();
        prepararModoAlta();
        configurarEventos();
    }

    private void configurarEventos() {
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarCambios());

        // Solo en modo alta, el combo cambia el tipo
        if (modoAlta) {
            cmbTipoDeVino.addActionListener(e -> {
                String tipo = String.valueOf(cmbTipoDeVino.getSelectedItem());
                aplicarModoTipo(tipo);
            });
        }
    }

    private void inicializarCombos() {
        // Generales
        cargarComboEnum(cmbTipoCrianza, TipoCrianza.values());
        cargarComboEnum(cmbAroma, Aroma.values());
        cargarComboEnum(cmbBarrica, Barrica.values());

        // Años
        cmbAnio.removeAllItems();
        int anioActual = java.time.Year.now().getValue();
        for (int a = anioActual; a >= 1990; a--) {
            cmbAnio.addItem(String.valueOf(a));
        }

        // Stock (botellas)
        cmbStock.removeAllItems();
        for (int s = 0; s <= 500; s++) {
            cmbStock.addItem(String.valueOf(s));
        }

        // Bodegas (sin repetir)
        cmbBodega.removeAllItems();
        if (gestor != null) {
            for (String b : obtenerBodegasUnicas()) {
                cmbBodega.addItem(b);
            }
        }
        cmbBodega.setEditable(true);

        // Tipo de vino (en edición lo bloqueamos)
        cmbTipoDeVino.removeAllItems();
        cmbTipoDeVino.addItem("TINTO");
        cmbTipoDeVino.addItem("BLANCO");
        cmbTipoDeVino.addItem("ROSADO");
        cmbTipoDeVino.setEnabled(modoAlta);

        // Específicos tinto
        cargarComboEnum(cmbUvaTinta, UvaTinta.values());
        cargarComboEnum(cmbTaninos, NivelTaninos.values());
        cargarComboEnum(cmbCuerpo, Cuerpo.values());

        // Específicos blanco
        cargarComboEnum(cmbUvaBlanca, UvaBlanca.values());
        cargarComboEnum(cmbDulzorBlanco, Dulzor.values());
        cargarComboEnum(cmbAcidez, Acidez.values());

        cmbTempServicio.removeAllItems();
        for (int t = 0; t <= 20; t++) {
            cmbTempServicio.addItem(String.valueOf(t));
        }

        // Específicos rosado
        cargarComboEnum(cmbUvaBase, UvaTinta.values());
        cargarComboEnum(cmbMetodo, MetodoRosado.values());
        cargarComboEnum(cmbDulzor, Dulzor.values());
    }

    private <E extends Enum<E>> void cargarComboEnum(JComboBox<String> combo, E[] valores) {
        combo.removeAllItems();
        for (E v : valores) {
            combo.addItem(v.name());
        }
    }

    private List<String> obtenerBodegasUnicas() {
        java.util.Set<String> set = new java.util.TreeSet<>();
        for (Vino v : gestor.listar()) {
            if (v.getBodega() != null && !v.getBodega().isBlank()) {
                set.add(v.getBodega().trim());
            }
        }
        return new ArrayList<>(set);
    }

    private void cargarDatosEnPantalla() {
        if (vinoOriginal == null) {
            return;
        }

        // Generales
        txtNombre.setText(vinoOriginal.getNombre());
        cmbBodega.setSelectedItem(vinoOriginal.getBodega());
        cmbAnio.setSelectedItem(String.valueOf(vinoOriginal.getAnioProduccion()));
        cmbTipoCrianza.setSelectedItem(vinoOriginal.getTipoCrianza().name());
        cmbAroma.setSelectedItem(vinoOriginal.getAroma().name());
        cmbBarrica.setSelectedItem(vinoOriginal.getBarrica().name());
        txtPrecio.setText(String.valueOf(vinoOriginal.getPrecio()));
        cmbStock.setSelectedItem(String.valueOf(vinoOriginal.getStockBotellas()));

        // Tipo + específicos
        if (vinoOriginal instanceof Tinto t) {
            cmbTipoDeVino.setSelectedItem("TINTO");
            cmbUvaTinta.setSelectedItem(t.getUva().name());
            cmbTaninos.setSelectedItem(t.getTaninos().name());
            cmbCuerpo.setSelectedItem(t.getCuerpo().name());
            aplicarModoTipo("TINTO");

        } else if (vinoOriginal instanceof Blanco b) {
            cmbTipoDeVino.setSelectedItem("BLANCO");
            cmbUvaBlanca.setSelectedItem(b.getUva().name());
            cmbDulzorBlanco.setSelectedItem(b.getDulzor().name());
            cmbAcidez.setSelectedItem(b.getAcidez().name());
            cmbTempServicio.setSelectedItem(String.valueOf((int) b.getTempServicioC()));
            aplicarModoTipo("BLANCO");

        } else if (vinoOriginal instanceof Rosado r) {
            cmbTipoDeVino.setSelectedItem("ROSADO");
            cmbUvaBase.setSelectedItem(r.getUvaBase().name());
            cmbMetodo.setSelectedItem(r.getMetodo().name());
            cmbDulzor.setSelectedItem(r.getDulzor().name());
            aplicarModoTipo("ROSADO");
        }
    }

    private void aplicarModoTipo(String tipo) {
        boolean esTinto = "TINTO".equals(tipo);
        boolean esBlanco = "BLANCO".equals(tipo);
        boolean esRosado = "ROSADO".equals(tipo);

        // Tinto
        cmbUvaTinta.setEnabled(esTinto);
        cmbTaninos.setEnabled(esTinto);
        cmbCuerpo.setEnabled(esTinto);
        lblUvaTinta.setEnabled(esTinto);
        lblTaninos.setEnabled(esTinto);
        lblCuerpo.setEnabled(esTinto);
        lblTinto.setEnabled(esTinto);

        // Blanco
        cmbUvaBlanca.setEnabled(esBlanco);
        cmbDulzorBlanco.setEnabled(esBlanco);
        cmbAcidez.setEnabled(esBlanco);
        cmbTempServicio.setEnabled(esBlanco);
        lblUvaBlanca.setEnabled(esBlanco);
        lblDulzorBlanco.setEnabled(esBlanco);
        lblAcidez.setEnabled(esBlanco);
        lblTempServicio.setEnabled(esBlanco);
        lblBlanco.setEnabled(esBlanco);

        // Rosado
        cmbUvaBase.setEnabled(esRosado);
        cmbMetodo.setEnabled(esRosado);
        cmbDulzor.setEnabled(esRosado);
        lblUvaBase.setEnabled(esRosado);
        lblMetodo.setEnabled(esRosado);
        lblDulzor.setEnabled(esRosado);
        lblRosado.setEnabled(esRosado);
    }

    private void guardarCambios() {
        try {
            if (gestor == null) {
                JOptionPane.showMessageDialog(this, "Gestor no disponible.");
                return;
            }

            // Datos generales
            int id = modoAlta ? gestor.generarId() : vinoOriginal.getId();

            String nombre = txtNombre.getText().trim();
            String bodega = String.valueOf(cmbBodega.getSelectedItem()).trim();

            if (nombre.isBlank()) {
                JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
                return;
            }
            if (bodega.isBlank()) {
                JOptionPane.showMessageDialog(this, "La bodega es obligatoria.");
                return;
            }

            int anio = Integer.parseInt(String.valueOf(cmbAnio.getSelectedItem()));
            TipoCrianza tipoCrianza = TipoCrianza.valueOf(String.valueOf(cmbTipoCrianza.getSelectedItem()));
            Aroma aroma = Aroma.valueOf(String.valueOf(cmbAroma.getSelectedItem()));
            Barrica barrica = Barrica.valueOf(String.valueOf(cmbBarrica.getSelectedItem()));

            double precio = Double.parseDouble(txtPrecio.getText().trim().replace(",", "."));
            int stock = Integer.parseInt(String.valueOf(cmbStock.getSelectedItem()));

            String tipoSeleccionado = String.valueOf(cmbTipoDeVino.getSelectedItem());
            Vino vinoNuevo;

            if ("TINTO".equals(tipoSeleccionado)) {
                UvaTinta uva = UvaTinta.valueOf(String.valueOf(cmbUvaTinta.getSelectedItem()));
                NivelTaninos taninos = NivelTaninos.valueOf(String.valueOf(cmbTaninos.getSelectedItem()));
                Cuerpo cuerpo = Cuerpo.valueOf(String.valueOf(cmbCuerpo.getSelectedItem()));

                vinoNuevo = new Tinto(id, nombre, bodega, anio, tipoCrianza, aroma, barrica, precio, stock, uva, taninos, cuerpo);

            } else if ("BLANCO".equals(tipoSeleccionado)) {
                UvaBlanca uva = UvaBlanca.valueOf(String.valueOf(cmbUvaBlanca.getSelectedItem()));
                Dulzor dulzorBlanco = Dulzor.valueOf(String.valueOf(cmbDulzorBlanco.getSelectedItem()));
                Acidez acidez = Acidez.valueOf(String.valueOf(cmbAcidez.getSelectedItem()));
                double tempServicio = Double.parseDouble(String.valueOf(cmbTempServicio.getSelectedItem()));

                vinoNuevo = new Blanco(id, nombre, bodega, anio, tipoCrianza, aroma, barrica, precio, stock, uva, dulzorBlanco, acidez, tempServicio);

            } else if ("ROSADO".equals(tipoSeleccionado)) {
                UvaTinta uvaBase = UvaTinta.valueOf(String.valueOf(cmbUvaBase.getSelectedItem()));
                MetodoRosado metodo = MetodoRosado.valueOf(String.valueOf(cmbMetodo.getSelectedItem()));
                Dulzor dulzorRosado = Dulzor.valueOf(String.valueOf(cmbDulzor.getSelectedItem()));

                vinoNuevo = new Rosado(id, nombre, bodega, anio, tipoCrianza, aroma, barrica, precio, stock, uvaBase, metodo, dulzorRosado);

            } else {
                JOptionPane.showMessageDialog(this, "Tipo de vino no soportado.");
                return;
            }

            boolean ok;
            if (modoAlta) {
                ok = gestor.agregar(vinoNuevo);      // ✅ alta
            } else {
                ok = gestor.actualizar(id, vinoNuevo); // ✅ edición
            }

            if (ok) {
                JOptionPane.showMessageDialog(this, modoAlta ? "Vino cargado correctamente." : "Vino actualizado correctamente.");
                if (alGuardar != null) {
                    alGuardar.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, modoAlta ? "No se pudo cargar el vino." : "No se pudo actualizar el vino.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Precio/Año/Stock inválido.");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
        }
    }

    private void prepararModoAlta() {
        setTitle("Cargar vino");

        txtNombre.setText("");
        txtPrecio.setText("");

        if (cmbBodega.getItemCount() > 0) {
            cmbBodega.setSelectedIndex(0);
        }
        if (cmbAnio.getItemCount() > 0) {
            cmbAnio.setSelectedItem(String.valueOf(java.time.Year.now().getValue()));
        }
        if (cmbTipoCrianza.getItemCount() > 0) {
            cmbTipoCrianza.setSelectedIndex(0);
        }
        if (cmbAroma.getItemCount() > 0) {
            cmbAroma.setSelectedIndex(0);
        }
        if (cmbBarrica.getItemCount() > 0) {
            cmbBarrica.setSelectedIndex(0);
        }
        if (cmbStock.getItemCount() > 0) {
            cmbStock.setSelectedItem("0");
        }

        cmbTipoDeVino.setEnabled(true);
        cmbTipoDeVino.setSelectedItem("TINTO");
        aplicarModoTipo("TINTO");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanelDatosGenerales = new javax.swing.JPanel();
        lbldatosgenerales = new javax.swing.JLabel();
        lblNombre = new javax.swing.JLabel();
        lblAnio = new javax.swing.JLabel();
        lblBarrica = new javax.swing.JLabel();
        lblBodega = new javax.swing.JLabel();
        lblTipoCrianza = new javax.swing.JLabel();
        lblPrecio = new javax.swing.JLabel();
        lblAroma = new javax.swing.JLabel();
        lblStock = new javax.swing.JLabel();
        lblBotellas = new javax.swing.JLabel();
        cmbAnio = new javax.swing.JComboBox<>();
        cmbBarrica = new javax.swing.JComboBox<>();
        cmbTipoCrianza = new javax.swing.JComboBox<>();
        cmbAroma = new javax.swing.JComboBox<>();
        cmbStock = new javax.swing.JComboBox<>();
        txtPrecio = new javax.swing.JTextField();
        cmbBodega = new javax.swing.JComboBox<>();
        txtNombre = new javax.swing.JTextField();
        imgLogo = new javax.swing.JLabel();
        jPanelDatosEspecificos = new javax.swing.JPanel();
        JPanelTipoVino = new javax.swing.JPanel();
        cmbTipoDeVino = new javax.swing.JComboBox<>();
        lblTipoVino = new javax.swing.JLabel();
        jPanelContenedorEspecifico = new javax.swing.JPanel();
        jPanelContenedorVinoBlancoRosado = new javax.swing.JPanel();
        jPanelRosado = new javax.swing.JPanel();
        lblUvaBase = new javax.swing.JLabel();
        lblMetodo = new javax.swing.JLabel();
        lblDulzor = new javax.swing.JLabel();
        cmbUvaBase = new javax.swing.JComboBox<>();
        cmbDulzor = new javax.swing.JComboBox<>();
        cmbMetodo = new javax.swing.JComboBox<>();
        lblRosado = new javax.swing.JLabel();
        lblUvaBlanca = new javax.swing.JLabel();
        lblDulzorBlanco = new javax.swing.JLabel();
        lblAcidez = new javax.swing.JLabel();
        lblTempServicio = new javax.swing.JLabel();
        cmbAcidez = new javax.swing.JComboBox<>();
        cmbUvaBlanca = new javax.swing.JComboBox<>();
        cmbDulzorBlanco = new javax.swing.JComboBox<>();
        cmbTempServicio = new javax.swing.JComboBox<>();
        lblBlanco = new javax.swing.JLabel();
        lblUvaTinta = new javax.swing.JLabel();
        lblTaninos = new javax.swing.JLabel();
        lblCuerpo = new javax.swing.JLabel();
        cmbUvaTinta = new javax.swing.JComboBox<>();
        cmbCuerpo = new javax.swing.JComboBox<>();
        cmbTaninos = new javax.swing.JComboBox<>();
        lblTinto = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanelDatosGenerales.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        lbldatosgenerales.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbldatosgenerales.setText("Datos Generales");

        lblNombre.setText(" Nombre:");

        lblAnio.setText("Año:");

        lblBarrica.setText("Barrica:");

        lblBodega.setText("Bodega:");

        lblTipoCrianza.setText("Tipo Crianza:");

        lblPrecio.setText("Precio:");

        lblAroma.setText("Aroma:");

        lblStock.setText("Stock:");

        lblBotellas.setText("botellas");

        cmbAnio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbBarrica.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbTipoCrianza.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbAroma.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbStock.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtPrecio.setText("jTextField1");

        cmbBodega.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtNombre.setText("jTextField1");

        imgLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/logo2.png"))); // NOI18N

        javax.swing.GroupLayout jPanelDatosGeneralesLayout = new javax.swing.GroupLayout(jPanelDatosGenerales);
        jPanelDatosGenerales.setLayout(jPanelDatosGeneralesLayout);
        jPanelDatosGeneralesLayout.setHorizontalGroup(
            jPanelDatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDatosGeneralesLayout.createSequentialGroup()
                .addGroup(jPanelDatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelDatosGeneralesLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jPanelDatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelDatosGeneralesLayout.createSequentialGroup()
                                .addGroup(jPanelDatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelDatosGeneralesLayout.createSequentialGroup()
                                        .addComponent(lblAnio)
                                        .addGap(36, 36, 36)
                                        .addComponent(cmbAnio, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanelDatosGeneralesLayout.createSequentialGroup()
                                        .addComponent(lblBarrica)
                                        .addGap(22, 22, 22)
                                        .addComponent(cmbBarrica, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(1, 1, 1)
                                .addGroup(jPanelDatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTipoCrianza, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblPrecio, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGroup(jPanelDatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanelDatosGeneralesLayout.createSequentialGroup()
                                        .addGap(12, 12, 12)
                                        .addComponent(cmbTipoCrianza, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(111, 111, 111)
                                        .addComponent(lblAroma))
                                    .addGroup(jPanelDatosGeneralesLayout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblStock)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanelDatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanelDatosGeneralesLayout.createSequentialGroup()
                                        .addComponent(cmbStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(lblBotellas))
                                    .addComponent(cmbAroma, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanelDatosGeneralesLayout.createSequentialGroup()
                                .addComponent(lblNombre)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(lblBodega)
                                .addGap(18, 18, 18)
                                .addComponent(cmbBodega, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addComponent(imgLogo))
                    .addGroup(jPanelDatosGeneralesLayout.createSequentialGroup()
                        .addGap(395, 395, 395)
                        .addComponent(lbldatosgenerales)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanelDatosGeneralesLayout.setVerticalGroup(
            jPanelDatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDatosGeneralesLayout.createSequentialGroup()
                .addGroup(jPanelDatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelDatosGeneralesLayout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lbldatosgenerales)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelDatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblNombre)
                            .addComponent(lblBodega)
                            .addComponent(cmbBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(jPanelDatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblAnio)
                            .addComponent(lblTipoCrianza)
                            .addComponent(lblAroma)
                            .addComponent(cmbAnio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbTipoCrianza, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbAroma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelDatosGeneralesLayout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(imgLogo)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addGroup(jPanelDatosGeneralesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBarrica)
                    .addComponent(lblPrecio)
                    .addComponent(lblStock)
                    .addComponent(lblBotellas)
                    .addComponent(cmbBarrica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(24, 24, 24))
        );

        jPanelDatosEspecificos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanelDatosEspecificos.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);

        cmbTipoDeVino.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblTipoVino.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTipoVino.setText("Tipo de vino:");

        javax.swing.GroupLayout JPanelTipoVinoLayout = new javax.swing.GroupLayout(JPanelTipoVino);
        JPanelTipoVino.setLayout(JPanelTipoVinoLayout);
        JPanelTipoVinoLayout.setHorizontalGroup(
            JPanelTipoVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelTipoVinoLayout.createSequentialGroup()
                .addGap(236, 236, 236)
                .addComponent(lblTipoVino)
                .addGap(43, 43, 43)
                .addComponent(cmbTipoDeVino, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        JPanelTipoVinoLayout.setVerticalGroup(
            JPanelTipoVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPanelTipoVinoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(JPanelTipoVinoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbTipoDeVino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTipoVino))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblUvaBase.setText("Uva Base:");

        lblMetodo.setText("Metodo:");

        lblDulzor.setText("Dulzor:");

        cmbUvaBase.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbDulzor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbMetodo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblRosado.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblRosado.setText("ROSADO");

        javax.swing.GroupLayout jPanelRosadoLayout = new javax.swing.GroupLayout(jPanelRosado);
        jPanelRosado.setLayout(jPanelRosadoLayout);
        jPanelRosadoLayout.setHorizontalGroup(
            jPanelRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRosadoLayout.createSequentialGroup()
                .addGroup(jPanelRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelRosadoLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanelRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblDulzor)
                            .addComponent(lblMetodo)
                            .addComponent(lblUvaBase))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanelRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbUvaBase, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbDulzor, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbMetodo, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanelRosadoLayout.createSequentialGroup()
                        .addGap(118, 118, 118)
                        .addComponent(lblRosado)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanelRosadoLayout.setVerticalGroup(
            jPanelRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelRosadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblRosado)
                .addGap(9, 9, 9)
                .addGroup(jPanelRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUvaBase)
                    .addComponent(cmbUvaBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanelRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMetodo)
                    .addComponent(cmbMetodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(jPanelRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDulzor)
                    .addComponent(cmbDulzor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblUvaBlanca.setText("Uva Blanca:");

        lblDulzorBlanco.setText("Dulzor:");

        lblAcidez.setText("Acidez:");

        lblTempServicio.setText("Temp. Servicio:");

        cmbAcidez.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbUvaBlanca.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbDulzorBlanco.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbTempServicio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblBlanco.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblBlanco.setText("BLANCO");

        javax.swing.GroupLayout jPanelContenedorVinoBlancoRosadoLayout = new javax.swing.GroupLayout(jPanelContenedorVinoBlancoRosado);
        jPanelContenedorVinoBlancoRosado.setLayout(jPanelContenedorVinoBlancoRosadoLayout);
        jPanelContenedorVinoBlancoRosadoLayout.setHorizontalGroup(
            jPanelContenedorVinoBlancoRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelContenedorVinoBlancoRosadoLayout.createSequentialGroup()
                .addGroup(jPanelContenedorVinoBlancoRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelContenedorVinoBlancoRosadoLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanelContenedorVinoBlancoRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelContenedorVinoBlancoRosadoLayout.createSequentialGroup()
                                .addComponent(lblTempServicio)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbTempServicio, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelContenedorVinoBlancoRosadoLayout.createSequentialGroup()
                                .addGroup(jPanelContenedorVinoBlancoRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblUvaBlanca)
                                    .addComponent(lblAcidez)
                                    .addComponent(lblDulzorBlanco))
                                .addGap(18, 18, 18)
                                .addGroup(jPanelContenedorVinoBlancoRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbDulzorBlanco, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbAcidez, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbUvaBlanca, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanelContenedorVinoBlancoRosadoLayout.createSequentialGroup()
                        .addGap(146, 146, 146)
                        .addComponent(lblBlanco)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addComponent(jPanelRosado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(81, 81, 81))
        );
        jPanelContenedorVinoBlancoRosadoLayout.setVerticalGroup(
            jPanelContenedorVinoBlancoRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelRosado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanelContenedorVinoBlancoRosadoLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(lblBlanco)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelContenedorVinoBlancoRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUvaBlanca)
                    .addComponent(cmbUvaBlanca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jPanelContenedorVinoBlancoRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDulzorBlanco)
                    .addComponent(cmbDulzorBlanco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanelContenedorVinoBlancoRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAcidez)
                    .addComponent(cmbAcidez, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanelContenedorVinoBlancoRosadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTempServicio)
                    .addComponent(cmbTempServicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblUvaTinta.setText("Uva Tinta:");

        lblTaninos.setText("Taninos:");

        lblCuerpo.setText("Cuerpo:");

        cmbUvaTinta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbCuerpo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbTaninos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblTinto.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTinto.setText("TINTO");

        javax.swing.GroupLayout jPanelContenedorEspecificoLayout = new javax.swing.GroupLayout(jPanelContenedorEspecifico);
        jPanelContenedorEspecifico.setLayout(jPanelContenedorEspecificoLayout);
        jPanelContenedorEspecificoLayout.setHorizontalGroup(
            jPanelContenedorEspecificoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContenedorEspecificoLayout.createSequentialGroup()
                .addGroup(jPanelContenedorEspecificoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelContenedorEspecificoLayout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanelContenedorEspecificoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanelContenedorEspecificoLayout.createSequentialGroup()
                                .addComponent(lblUvaTinta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbUvaTinta, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanelContenedorEspecificoLayout.createSequentialGroup()
                                .addGroup(jPanelContenedorEspecificoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblCuerpo)
                                    .addComponent(lblTaninos))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanelContenedorEspecificoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbCuerpo, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbTaninos, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanelContenedorEspecificoLayout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(lblTinto)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelContenedorVinoBlancoRosado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67))
        );
        jPanelContenedorEspecificoLayout.setVerticalGroup(
            jPanelContenedorEspecificoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelContenedorEspecificoLayout.createSequentialGroup()
                .addGroup(jPanelContenedorEspecificoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelContenedorEspecificoLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(lblTinto)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanelContenedorEspecificoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblUvaTinta)
                            .addComponent(cmbUvaTinta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(41, 41, 41)
                        .addGroup(jPanelContenedorEspecificoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTaninos)
                            .addComponent(cmbTaninos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(41, 41, 41)
                        .addGroup(jPanelContenedorEspecificoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCuerpo)
                            .addComponent(cmbCuerpo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jPanelContenedorVinoBlancoRosado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 1, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanelDatosEspecificosLayout = new javax.swing.GroupLayout(jPanelDatosEspecificos);
        jPanelDatosEspecificos.setLayout(jPanelDatosEspecificosLayout);
        jPanelDatosEspecificosLayout.setHorizontalGroup(
            jPanelDatosEspecificosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDatosEspecificosLayout.createSequentialGroup()
                .addComponent(JPanelTipoVino, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanelDatosEspecificosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanelContenedorEspecifico, javax.swing.GroupLayout.PREFERRED_SIZE, 962, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanelDatosEspecificosLayout.setVerticalGroup(
            jPanelDatosEspecificosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelDatosEspecificosLayout.createSequentialGroup()
                .addComponent(JPanelTipoVino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelContenedorEspecifico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/guardar.png"))); // NOI18N
        btnGuardar.setText("GUARDAR");

        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/recursos/cancelar.png"))); // NOI18N
        btnCancelar.setText("CANCELAR");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanelDatosEspecificos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jPanelDatosGenerales, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(220, 220, 220)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(157, 157, 157)
                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanelDatosGenerales, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanelDatosEspecificos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPanelTipoVino;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox<String> cmbAcidez;
    private javax.swing.JComboBox<String> cmbAnio;
    private javax.swing.JComboBox<String> cmbAroma;
    private javax.swing.JComboBox<String> cmbBarrica;
    private javax.swing.JComboBox<String> cmbBodega;
    private javax.swing.JComboBox<String> cmbCuerpo;
    private javax.swing.JComboBox<String> cmbDulzor;
    private javax.swing.JComboBox<String> cmbDulzorBlanco;
    private javax.swing.JComboBox<String> cmbMetodo;
    private javax.swing.JComboBox<String> cmbStock;
    private javax.swing.JComboBox<String> cmbTaninos;
    private javax.swing.JComboBox<String> cmbTempServicio;
    private javax.swing.JComboBox<String> cmbTipoCrianza;
    private javax.swing.JComboBox<String> cmbTipoDeVino;
    private javax.swing.JComboBox<String> cmbUvaBase;
    private javax.swing.JComboBox<String> cmbUvaBlanca;
    private javax.swing.JComboBox<String> cmbUvaTinta;
    private javax.swing.JLabel imgLogo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelContenedorEspecifico;
    private javax.swing.JPanel jPanelContenedorVinoBlancoRosado;
    private javax.swing.JPanel jPanelDatosEspecificos;
    private javax.swing.JPanel jPanelDatosGenerales;
    private javax.swing.JPanel jPanelRosado;
    private javax.swing.JLabel lblAcidez;
    private javax.swing.JLabel lblAnio;
    private javax.swing.JLabel lblAroma;
    private javax.swing.JLabel lblBarrica;
    private javax.swing.JLabel lblBlanco;
    private javax.swing.JLabel lblBodega;
    private javax.swing.JLabel lblBotellas;
    private javax.swing.JLabel lblCuerpo;
    private javax.swing.JLabel lblDulzor;
    private javax.swing.JLabel lblDulzorBlanco;
    private javax.swing.JLabel lblMetodo;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblRosado;
    private javax.swing.JLabel lblStock;
    private javax.swing.JLabel lblTaninos;
    private javax.swing.JLabel lblTempServicio;
    private javax.swing.JLabel lblTinto;
    private javax.swing.JLabel lblTipoCrianza;
    private javax.swing.JLabel lblTipoVino;
    private javax.swing.JLabel lblUvaBase;
    private javax.swing.JLabel lblUvaBlanca;
    private javax.swing.JLabel lblUvaTinta;
    private javax.swing.JLabel lbldatosgenerales;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecio;
    // End of variables declaration//GEN-END:variables
}
