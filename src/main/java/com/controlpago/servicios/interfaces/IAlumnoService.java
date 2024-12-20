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

    void eliminarPorId(Integer id);

    Page<Alumno> buscarPorCriterios(String nombre, String apellido, Integer gradoId, Pageable pageable);


    Page<Alumno> buscarPorNombreCompletoYGrado(String nombreCompleto, Integer gradoId, Pageable pageable);


    long contarAlumnos(); // Existing method
    long contarTotalAlumnos(); // New method
}
