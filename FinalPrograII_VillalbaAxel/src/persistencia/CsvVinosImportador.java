package persistencia;

import modelo.*;
import servicio.GestorVinos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvVinosImportador {

    public static class ResultadoImportacion {
        private int cargados;
        private int errores;
        private final List<String> detalleErrores = new ArrayList<>();

        public int getCargados() {
            return cargados;
        }

        public int getErrores() {
            return errores;
        }

        public List<String> getDetalleErrores() {
            return detalleErrores;
        }

        private void sumarCargado() {
            cargados++;
        }

        private void sumarError(String mensaje) {
            errores++;
            detalleErrores.add(mensaje);
        }
    }

    public static ResultadoImportacion importar(File archivo, GestorVinos gestor) throws Exception {
        ResultadoImportacion resultado = new ResultadoImportacion();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int nroLinea = 0;

            while ((linea = br.readLine()) != null) {
                nroLinea++;

                if (linea.trim().isEmpty()) {
                    continue;
                }

                // Saltar cabecera si existe
                if (nroLinea == 1 && linea.toLowerCase().contains("tipo;")) {
                    continue;
                }

                try {
                    Vino vino = parsearLinea(linea, gestor);
                    boolean ok = gestor.agregarOSumarStock(vino);

                    if (ok) {
                        resultado.sumarCargado();
                    } else {
                        resultado.sumarError("Línea " + nroLinea + ": ID duplicado o no se pudo agregar.");
                    }

                } catch (Exception e) {
                    resultado.sumarError("Línea " + nroLinea + ": " + e.getMessage());
                }
            }
        }

        return resultado;
    }

    private static Vino parsearLinea(String linea, GestorVinos gestor) {
        // Usamos -1 para conservar columnas vacías al final
        String[] c = linea.split(";", -1);

        // Esperamos 16 columnas
        // 0 tipo
        // 1 nombre
        // 2 bodega
        // 3 anio
        // 4 tipoCrianza
        // 5 aroma
        // 6 barrica
        // 7 precio
        // 8 stock
        // 9 uva
        // 10 taninos
        // 11 cuerpo
        // 12 dulzor
        // 13 acidez
        // 14 tempServicio
        // 15 metodoRosado
        if (c.length < 16) {
            throw new IllegalArgumentException("Cantidad de columnas inválida. Esperadas: 16, recibidas: " + c.length);
        }

        String tipo = c[0].trim().toUpperCase();
        String nombre = c[1].trim();
        String bodega = c[2].trim();
        int anio = Integer.parseInt(c[3].trim());
        TipoCrianza tipoCrianza = TipoCrianza.valueOf(c[4].trim().toUpperCase());
        Aroma aroma = Aroma.valueOf(c[5].trim().toUpperCase());
        Barrica barrica = Barrica.valueOf(c[6].trim().toUpperCase());

        double precio = Double.parseDouble(c[7].trim().replace(",", "."));
        int stock = Integer.parseInt(c[8].trim());

        int id = gestor.generarId();

        switch (tipo) {
            case "TINTO" -> {
                UvaTinta uva = UvaTinta.valueOf(c[9].trim().toUpperCase());
                NivelTaninos taninos = NivelTaninos.valueOf(c[10].trim().toUpperCase());
                Cuerpo cuerpo = Cuerpo.valueOf(c[11].trim().toUpperCase());

                return new Tinto(id, nombre, bodega, anio, tipoCrianza, aroma, barrica, precio, stock, uva, taninos, cuerpo);
            }

            case "BLANCO" -> {
                UvaBlanca uva = UvaBlanca.valueOf(c[9].trim().toUpperCase());
                Dulzor dulzor = Dulzor.valueOf(c[12].trim().toUpperCase());
                Acidez acidez = Acidez.valueOf(c[13].trim().toUpperCase());

                String tempTxt = c[14].trim();
                if (tempTxt.isEmpty()) {
                    throw new IllegalArgumentException("Temp. de servicio requerida para BLANCO");
                }
                double tempServicio = Double.parseDouble(tempTxt.replace(",", "."));

                return new Blanco(id, nombre, bodega, anio, tipoCrianza, aroma, barrica, precio, stock, uva, dulzor, acidez, tempServicio);
            }

            case "ROSADO" -> {
                UvaTinta uvaBase = UvaTinta.valueOf(c[9].trim().toUpperCase());
                Dulzor dulzor = Dulzor.valueOf(c[12].trim().toUpperCase());

                String metodoTxt = c[15].trim();
                if (metodoTxt.isEmpty()) {
                    throw new IllegalArgumentException("Método rosado requerido para ROSADO");
                }
                MetodoRosado metodo = MetodoRosado.valueOf(metodoTxt.toUpperCase());

                return new Rosado(id, nombre, bodega, anio, tipoCrianza, aroma, barrica, precio, stock, uvaBase, metodo, dulzor);
            }

            default -> throw new IllegalArgumentException("Tipo inválido: " + tipo);
        }
    }
}