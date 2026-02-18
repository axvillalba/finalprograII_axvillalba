package servicio;

import java.util.List;
import java.util.Optional;

public interface ICrud<T> {

    boolean agregar(T obj);

    List<T> listar();

    Optional<T> buscarPorId(int id);

    boolean actualizar(int id, T nuevo);

    boolean eliminar(int id);
}
