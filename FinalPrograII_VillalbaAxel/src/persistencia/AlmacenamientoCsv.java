package persistencia;

import modelo.Vino;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class AlmacenamientoCsv {

    public void exportar(Path archivo, List<Vino> inventario) throws Exception {
        List<String> lineas = new ArrayList<>();
        lineas.add("tipo,id,nombre,bodega,anio,tipoCrianza,aroma,barrica,precio,stock,extra1,extra2,extra3,extra4");
        for (Vino v : inventario) {
            lineas.add(MapeadorCsv.aCsv(v));
        }
        Files.write(archivo, lineas, StandardCharsets.UTF_8);
    }

    public List<Vino> importar(Path archivo) throws Exception {
        List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);
        List<Vino> vinos = new ArrayList<>();
        for (int i = 1; i < lineas.size(); i++) {
            String l = lineas.get(i).trim();
            if (l.isEmpty()) {
                continue;
            }
            vinos.add(MapeadorCsv.desdeCsv(l));
        }
        return vinos;
    }
}
