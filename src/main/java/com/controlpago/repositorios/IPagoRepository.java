package com.controlpago.repositorios;

import com.controlpago.modelos.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IPagoRepository extends JpaRepository<Pago, Integer> {
    Pago findByAlumnoIdAndFechaBetween(Long alumnoId, LocalDate inicioMes, LocalDate finMes);
    List<Pago> findByAlumnoId(Integer alumnoId);

}
