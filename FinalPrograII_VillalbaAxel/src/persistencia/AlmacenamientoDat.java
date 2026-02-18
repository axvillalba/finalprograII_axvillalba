package persistencia;

import modelo.Vino;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AlmacenamientoDat {

    public void guardar(Path archivo, List<Vino> inventario) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(archivo))) {
            oos.writeObject(new PaqueteDat(inventario));
        }
    }

    public List<Vino> cargar(Path archivo) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(archivo))) {
            PaqueteDat p = (PaqueteDat) ois.readObject();
            return p.getInventario();
        }
    }
}