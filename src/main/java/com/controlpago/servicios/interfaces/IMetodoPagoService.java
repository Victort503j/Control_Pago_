package com.controlpago.servicios.interfaces;

import com.controlpago.modelos.MetodoPago;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IMetodoPagoService {
    Page<MetodoPago> buscarTodosPaginados(Pageable pageable);

    List<MetodoPago> obtenerTodos();

    Optional<MetodoPago> buscarPorId(Integer id);

    MetodoPago crearOEditar(MetodoPago metodoPago);

    void elimanarPorId(Integer id);
}
