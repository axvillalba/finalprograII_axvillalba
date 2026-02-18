package persistencia;

import modelo.*;

import java.util.ArrayList;
import java.util.Locale;

public class MapeadorCsv {

    private MapeadorCsv() {
    }

    // tipo,id,nombre,bodega,anio,tipoCrianza,aroma,barrica,precio,stock,extra1,extra2,extra3,extra4
    public static String aCsv(Vino v) {
        String tipo = v.getClass().getSimpleName().toUpperCase(Locale.ROOT);

        String base = String.join(",",
                tipo,
                String.valueOf(v.getId()),
                escape(v.getNombre()),
                escape(v.getBodega()),
                String.valueOf(v.getAnioProduccion()),
                v.getTipoCrianza().name(),
                v.getAroma().name(),
                v.getBarrica().name(),
                String.valueOf(v.getPrecio()),
                String.valueOf(v.getStockBotellas())
        );

        String extras = switch (tipo) {
            case "TINTO" -> {
                Tinto t = (Tinto) v;
                yield String.join(",", t.getUva().name(), t.getTaninos().name(), t.getCuerpo().name(), "");
            }
            case "BLANCO" -> {
                Blanco b = (Blanco) v;
                yield String.join(",", b.getUva().name(), b.getDulzor().name(), b.getAcidez().name(), String.valueOf(b.getTempServicioC()));
            }
            case "ROSADO" -> {
                Rosado r = (Rosado) v;
                yield String.join(",", r.getUvaBase().name(), r.getMetodo().name(), r.getDulzor().name(), "");
            }
            default ->
                ",,,";
        };

        return base + "," + extras;
    }

    public static Vino desdeCsv(String linea) {
        String[] p = splitCsv(linea);
        if (p.length < 10) {
            throw new IllegalArgumentException("CSV inválido: faltan campos");
        }

        String tipo = p[0].trim().toUpperCase(Locale.ROOT);
        int id = Integer.parseInt(p[1]);
        String nombre = unescape(p[2]);
        String bodega = unescape(p[3]);
        int anio = Integer.parseInt(p[4]);

        TipoCrianza tc = TipoCrianza.valueOf(p[5]);
        Aroma aroma = Aroma.valueOf(p[6]);
        Barrica barrica = Barrica.valueOf(p[7]);
        double precio = Double.parseDouble(p[8]);
        int stock = Integer.parseInt(p[9]);

        String e1 = p.length > 10 ? p[10].trim() : "";
        String e2 = p.length > 11 ? p[11].trim() : "";
        String e3 = p.length > 12 ? p[12].trim() : "";
        String e4 = p.length > 13 ? p[13].trim() : "";

        return switch (tipo) {
            case "TINTO" ->
                new Tinto(id, nombre, bodega, anio, tc, aroma, barrica, precio, stock,
                UvaTinta.valueOf(e1), NivelTaninos.valueOf(e2), Cuerpo.valueOf(e3));
            case "BLANCO" ->
                new Blanco(id, nombre, bodega, anio, tc, aroma, barrica, precio, stock,
                UvaBlanca.valueOf(e1), Dulzor.valueOf(e2), Acidez.valueOf(e3), Double.parseDouble(e4));
            case "ROSADO" ->
                new Rosado(id, nombre, bodega, anio, tc, aroma, barrica, precio, stock,
                UvaTinta.valueOf(e1), MetodoRosado.valueOf(e2), Dulzor.valueOf(e3));
            default ->
                throw new IllegalArgumentException("Tipo desconocido: " + tipo);
        };
    }

    private static String escape(String s) {
        if (s == null) {
            return "";
        }
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private static String unescape(String s) {
        if (s == null) {
            return "";
        }
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1).replace("\"\"", "\"");
        }
        return s;
    }

    private static String[] splitCsv(String line) {
        ArrayList<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                    cur.append('\"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString());
        return out.toArray(new String[0]);
    }
}
