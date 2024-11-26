package com.controlpago.servicios.implementaciones;

import com.controlpago.modelos.MetodoPago;
import com.controlpago.repositorios.IMetodoPagoRepository;
import com.controlpago.servicios.interfaces.IMetodoPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MetodoPagoService implements IMetodoPagoService {
    @Autowired
    private IMetodoPagoRepository metodoPagoRepository;
    @Override
    public Page<MetodoPago> buscarTodosPaginados(Pageable pageable) {
        return metodoPagoRepository.findAll(pageable);
    }

    @Override
    public List<MetodoPago> obtenerTodos() {
        return metodoPagoRepository.findAll();
    }

    @Override
    public Optional<MetodoPago> buscarPorId(Integer id) {
        return metodoPagoRepository.findById(id);
    }

    @Override
    public MetodoPago crearOEditar(MetodoPago metodoPago) {
        return metodoPagoRepository.save(metodoPago);
    }

    @Override
    public void elimanarPorId(Integer id) {
        metodoPagoRepository.deleteById(id);
    }
}
