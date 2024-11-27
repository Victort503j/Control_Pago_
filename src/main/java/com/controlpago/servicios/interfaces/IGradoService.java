package com.controlpago.servicios.interfaces;

import com.controlpago.modelos.Grado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IGradoService {
    Page<Grado> buscarTodosPaginados(Pageable pageable);

    List<Grado> obtenerTodos();

    Optional<Grado> buscarPorId(Integer id);

    Grado crearOEditar(Grado grado);

    void eliminarPorId(Integer id);
    long contarGrados();
    long contarTotalGrados();

    Page<Grado> buscarPorNombrePaginado(String nombre, Pageable pageable);


}
