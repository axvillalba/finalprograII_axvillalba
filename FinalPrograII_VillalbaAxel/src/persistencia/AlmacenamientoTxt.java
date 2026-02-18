package persistencia;

import modelo.Vino;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlmacenamientoTxt {

    public void exportarInventario(Path archivo, List<Vino> inventario) throws Exception {
        List<String> lineas = new ArrayList<>();
        lineas.add("INVENTARIO VINOTECA");
        lineas.add("Fecha: " + LocalDate.now());
        lineas.add("--------------------------------------------------");
        for (Vino v : inventario) {
            lineas.add(v.ficha());
        }

        Files.write(archivo, lineas, StandardCharsets.UTF_8);
    }

    public void exportarFiltrado(Path archivo, String titulo, List<Vino> lista) throws Exception {
        List<String> lineas = new ArrayList<>();
        lineas.add(titulo);
        lineas.add("Fecha: " + LocalDate.now());
        lineas.add("Cantidad: " + lista.size());
        lineas.add("--------------------------------------------------");
        for (Vino v : lista) {
            lineas.add(v.ficha());
        }

        Files.write(archivo, lineas, StandardCharsets.UTF_8);
    }
}
