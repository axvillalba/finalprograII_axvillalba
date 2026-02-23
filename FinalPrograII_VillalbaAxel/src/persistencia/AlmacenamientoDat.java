package persistencia;

import modelo.Vino;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AlmacenamientoDat {

    private AlmacenamientoDat() {
        // Utilitaria: no se instancia
    }

    public static void guardar(File archivo, List<Vino> vinos) throws IOException {
        if (archivo == null) {
            throw new IllegalArgumentException("Archivo null");
        }
        if (vinos == null) {
            throw new IllegalArgumentException("Lista de vinos null");
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
            oos.writeObject(new ArrayList<>(vinos)); // copiamos por seguridad
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Vino> cargar(File archivo) throws IOException, ClassNotFoundException {
        if (archivo == null) {
            throw new IllegalArgumentException("Archivo null");
        }

        if (!archivo.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            Object obj = ois.readObject();

            if (!(obj instanceof List<?> lista)) {
                throw new IOException("El archivo .dat no contiene una lista válida");
            }

            List<Vino> vinos = new ArrayList<>();
            for (Object item : lista) {
                if (!(item instanceof Vino v)) {
                    throw new IOException("El archivo .dat contiene elementos no válidos");
                }
                vinos.add(v);
            }

            return vinos;
        }
    }
}