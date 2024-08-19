package com.controlpago.servicios.implementaciones;

import com.controlpago.modelos.Alumno;
import com.controlpago.modelos.Pago;
import com.controlpago.repositorios.IPagoRepository;
import com.controlpago.servicios.interfaces.IPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PagoService implements IPagoService {

    @Autowired
    private IPagoRepository pagoRepository;

    @Override
    public Page<Pago> buscarTodosPaginados(Pageable pageable) {
        return pagoRepository.findAll(pageable);
    }

    @Override
    public List<Pago> obtenerTodos() {
        return pagoRepository.findAll();
    }

    @Override
    public Optional<Pago> buscarPorId(Integer id) {
        return pagoRepository.findById(id);
    }

    @Override
    public Pago crearOEditar(Pago pago) {
        return pagoRepository.save(pago);
    }

    @Override
    public void eliminarPorId(Integer id) {
        pagoRepository.deleteById(id);
    }

    @Override
    public Pago buscarPorAlumnoYMes(Long alumnoId, LocalDate fecha) {
        LocalDate primerDiaMes = fecha.withDayOfMonth(1);
        LocalDate ultimoDiaMes = fecha.withDayOfMonth(fecha.lengthOfMonth());
        return pagoRepository.findByAlumnoIdAndFechaBetween(alumnoId, primerDiaMes, ultimoDiaMes);
    }

    @Override
    public List<Pago> buscarPagosPorAlumno(Integer alumnoId) {
        return pagoRepository.findByAlumnoId(alumnoId);
    }

}
