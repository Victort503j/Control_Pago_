package com.controlpago.servicios.interfaces;

import com.controlpago.modelos.Alumno;
import com.controlpago.modelos.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IRolService {
    Page<Rol> buscarTodosPaginados(Pageable pageable);

    List<Rol> obtenerTodos();

    Optional<Rol> buscarPorId(Integer id);

    Rol crearOEditar(Rol rol);

    void elimanarPorId(Integer id);
}
