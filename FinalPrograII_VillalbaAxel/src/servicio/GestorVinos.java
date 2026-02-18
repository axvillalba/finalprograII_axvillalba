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
}
