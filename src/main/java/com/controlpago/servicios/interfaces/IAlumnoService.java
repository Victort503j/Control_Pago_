package com.controlpago.servicios.interfaces;

import com.controlpago.modelos.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IAlumnoService {
    Page<Alumno> buscarTodosPaginados(Pageable pageable);

    List<Alumno> obtenerTodos();

    Optional<Alumno> buscarPorId(Integer id);

    Alumno crearOEditar(Alumno alumno);

    void elimanarPorId(Integer id);
}
