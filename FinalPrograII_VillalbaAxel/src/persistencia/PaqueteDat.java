package persistencia;

import modelo.Vino;

import java.io.Serializable;
import java.util.List;

public class PaqueteDat implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<Vino> inventario;

    public PaqueteDat(List<Vino> inventario) {
        this.inventario = inventario;
    }

    public List<Vino> getInventario() {
        return inventario;
    }
}
