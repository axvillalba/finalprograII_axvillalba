package persistencia;

import modelo.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ExportadorVinos {

    public static void exportarCSV(File archivo, List<Vino> vinos) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {

            // Cabecera (mismo formato que usás para importar, pero agregando ID)
            bw.write("id;tipo;nombre;bodega;anio;tipoCrianza;aroma;barrica;precio;stock;uva;taninos;cuerpo;dulzor;acidez;tempServicio;metodoRosado");
            bw.newLine();

            for (Vino v : vinos) {
                String tipo = obtenerTipo(v);

                String uva = "";
                String taninos = "";
                String cuerpo = "";
                String dulzor = "";
                String acidez = "";
                String tempServicio = "";
                String metodoRosado = "";

                if (v instanceof Tinto t) {
                    uva = t.getUva().name();
                    taninos = t.getTaninos().name();
                    cuerpo = t.getCuerpo().name();
                } else if (v instanceof Blanco b) {
                    uva = b.getUva().name();
                    dulzor = b.getDulzor().name();
                    acidez = b.getAcidez().name();
                    tempServicio = formatearDouble(b.getTempServicioC());
                } else if (v instanceof Rosado r) {
                    uva = r.getUvaBase().name();
                    dulzor = r.getDulzor().name();
                    metodoRosado = r.getMetodo().name();
                }

                String linea = String.join(";",
                        String.valueOf(v.getId()),
                        tipo,
                        limpiarCSV(v.getNombre()),
                        limpiarCSV(v.getBodega()),
                        String.valueOf(v.getAnioProduccion()),
                        v.getTipoCrianza().name(),
                        v.getAroma().name(),
                        v.getBarrica().name(),
                        formatearDouble(v.getPrecio()),
                        String.valueOf(v.getStockBotellas()),
                        uva,
                        taninos,
                        cuerpo,
                        dulzor,
                        acidez,
                        tempServicio,
                        metodoRosado
                );

                bw.write(linea);
                bw.newLine();
            }
        }
    }

    public static void exportarTXT(File archivo, List<Vino> vinos) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write("=============================================");
            bw.newLine();
            bw.write("         INFORME DE STOCK - VINOTECA");
            bw.newLine();
            bw.write("=============================================");
            bw.newLine();
            bw.write("Cantidad de vinos registrados: " + vinos.size());
            bw.newLine();
            bw.newLine();

            for (Vino v : vinos) {
                bw.write("ID: " + v.getId());
                bw.newLine();
                bw.write("Tipo: " + obtenerTipo(v));
                bw.newLine();
                bw.write("Nombre: " + v.getNombre());
                bw.newLine();
                bw.write("Bodega: " + v.getBodega());
                bw.newLine();
                bw.write("Año: " + v.getAnioProduccion());
                bw.newLine();
                bw.write("Tipo de crianza: " + v.getTipoCrianza());
                bw.newLine();
                bw.write("Aroma: " + v.getAroma());
                bw.newLine();
                bw.write("Barrica: " + v.getBarrica());
                bw.newLine();
                bw.write("Precio: $" + formatearDouble(v.getPrecio()));
                bw.newLine();
                bw.write("Stock: " + v.getStockBotellas() + " botellas");
                bw.newLine();

                // Datos específicos
                if (v instanceof Tinto t) {
                    bw.write("Uva tinta: " + t.getUva());
                    bw.newLine();
                    bw.write("Taninos: " + t.getTaninos());
                    bw.newLine();
                    bw.write("Cuerpo: " + t.getCuerpo());
                    bw.newLine();

                } else if (v instanceof Blanco b) {
                    bw.write("Uva blanca: " + b.getUva());
                    bw.newLine();
                    bw.write("Dulzor: " + b.getDulzor());
                    bw.newLine();
                    bw.write("Acidez: " + b.getAcidez());
                    bw.newLine();
                    bw.write("Temp. servicio: " + formatearDouble(b.getTempServicioC()) + " °C");
                    bw.newLine();

                } else if (v instanceof Rosado r) {
                    bw.write("Uva base: " + r.getUvaBase());
                    bw.newLine();
                    bw.write("Método rosado: " + r.getMetodo());
                    bw.newLine();
                    bw.write("Dulzor: " + r.getDulzor());
                    bw.newLine();
                }

                if (v instanceof Maridaje m) {
                    bw.write("Maridaje sugerido: " + m.sugerirMaridaje());
                    bw.newLine();
                }

                bw.write("---------------------------------------------");
                bw.newLine();
            }
        }
    }

    private static String obtenerTipo(Vino v) {
        if (v instanceof Tinto) return "TINTO";
        if (v instanceof Blanco) return "BLANCO";
        if (v instanceof Rosado) return "ROSADO";
        return "DESCONOCIDO";
    }

    private static String limpiarCSV(String texto) {
        if (texto == null) return "";
        // Evitamos romper el CSV si alguien puso ;
        return texto.replace(";", ",").trim();
    }

    private static String formatearDouble(double valor) {
        return String.format(Locale.US, "%.2f", valor);
    }
}