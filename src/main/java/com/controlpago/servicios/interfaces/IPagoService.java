package com.controlpago.servicios.interfaces;

import com.controlpago.modelos.Alumno;
import com.controlpago.modelos.Pago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPagoService {
    Page<Pago> buscarTodosPaginados(Pageable pageable);

    List<Pago> obtenerTodos();

    Optional<Pago> buscarPorId(Integer id);

    Pago crearOEditar(Pago pago);

    void eliminarPorId(Integer id);
    Pago buscarPorAlumnoYMes(Long alumnoId, LocalDate fecha);

    List<Pago> buscarPagosPorAlumno(Integer alumnoId);



}
