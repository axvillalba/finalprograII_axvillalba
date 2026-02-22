package servicio;

import modelo.Vino;

import java.util.*;
import java.util.stream.Collectors;

public class GestorVinos implements ICrud<Vino> {

    private final List<Vino> inventario = new ArrayList<>();
    private int nextId = 1;

    public int generarId() {
        return nextId++;
    }

    public void setInventario(List<Vino> vinos) {
        inventario.clear();
        inventario.addAll(vinos);
        int maxId = inventario.stream().mapToInt(Vino::getId).max().orElse(0);
        nextId = maxId + 1;
    }

    public List<Vino> getInventario() {
        return Collections.unmodifiableList(inventario);
    }

    @Override
    public boolean agregar(Vino obj) {
        Objects.requireNonNull(obj, "Vino nulo");
        if (inventario.stream().anyMatch(v -> v.getId() == obj.getId())) {
            return false;
        }
        inventario.add(obj);
        nextId = Math.max(nextId, obj.getId() + 1);
        return true;
    }

    @Override
    public List<Vino> listar() {
        return getInventario();
    }

    @Override
    public Optional<Vino> buscarPorId(int id) {
        return inventario.stream().filter(v -> v.getId() == id).findFirst();
    }

    @Override
    public boolean actualizar(int id, Vino nuevo) {
        Objects.requireNonNull(nuevo, "Vino nuevo nulo");
        for (int i = 0; i < inventario.size(); i++) {
            if (inventario.get(i).getId() == id) {
                // Mantener ID consistente
                nuevo.setId(id);
                inventario.set(i, nuevo);
                nextId = Math.max(nextId, id + 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        return inventario.removeIf(v -> v.getId() == id);
    }

    public List<Vino> buscarPorNombre(String texto) {
        if (texto == null) {
            return List.of();
        }
        String t = texto.trim().toLowerCase();
        return inventario.stream()
                .filter(v -> v.getNombre().toLowerCase().contains(t))
                .collect(Collectors.toList());
    }

    public List<Vino> filtrarPorTipo(Class<? extends Vino> tipo) {
        return inventario.stream().filter(tipo::isInstance).collect(Collectors.toList());
    }


public boolean agregarOSumarStock(Vino nuevo) {
    Objects.requireNonNull(nuevo, "Vino nulo");

    Optional<Vino> existenteOpt = buscarVinoEquivalente(nuevo);

    if (existenteOpt.isPresent()) {
        Vino existente = existenteOpt.get();

        // Sumar stock
        int nuevoStock = existente.getStockBotellas() + nuevo.getStockBotellas();
        existente.setStockBotellas(nuevoStock);

        // Regla opcional: actualizar precio con el último importado
        existente.setPrecio(nuevo.getPrecio());

        return true;
    }

    // Si no existe equivalente, se agrega como nuevo
    return agregar(nuevo);
}

public Optional<Vino> buscarVinoEquivalente(Vino nuevo) {
    return inventario.stream()
            .filter(v -> sonEquivalentes(v, nuevo))
            .findFirst();
}

private boolean sonEquivalentes(Vino a, Vino b) {
    if (a == null || b == null) return false;

    // Deben ser del mismo subtipo
    if (!a.getClass().equals(b.getClass())) return false;

    // Base común (comparación normalizada)
    if (!normalizar(a.getNombre()).equals(normalizar(b.getNombre()))) return false;
    if (!normalizar(a.getBodega()).equals(normalizar(b.getBodega()))) return false;
    if (a.getAnioProduccion() != b.getAnioProduccion()) return false;
    if (a.getTipoCrianza() != b.getTipoCrianza()) return false;

    // Comparación por tipo específico
    if (a instanceof modelo.Tinto ta && b instanceof modelo.Tinto tb) {
        return ta.getUva() == tb.getUva();
    }

    if (a instanceof modelo.Blanco ba && b instanceof modelo.Blanco bb) {
        return ba.getUva() == bb.getUva();
    }

    if (a instanceof modelo.Rosado ra && b instanceof modelo.Rosado rb) {
        return ra.getUvaBase() == rb.getUvaBase()
                && ra.getMetodo() == rb.getMetodo();
    }

    return false;
}

private String normalizar(String s) {
    return s == null ? "" : s.trim().toLowerCase();
}
}