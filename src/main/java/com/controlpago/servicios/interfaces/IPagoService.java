package com.controlpago.servicios.interfaces;

import com.controlpago.modelos.Pago;
import com.controlpago.modelos.StudentPaymentRecord;
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
    List<Pago>buscarPagosPorStudentPaymnetRecord(Integer recordId);
    long contarPagos();
    long contarTotalPagos();

    long contarAlumnosQueHanPagado(LocalDate inicioMes, LocalDate finMes);
    long contarAlumnosQueNoHanPagado(LocalDate inicioMes, LocalDate finMes);

    // Nuevo método para contar los alumnos que han pagado por grado
    List<Object[]> contarAlumnosQueHanPagadoPorGrado(LocalDate inicioMes, LocalDate finMes);

    // Nuevo método para contar los alumnos que no han pagado por grado
    List<Object[]> contarAlumnosQueNoHanPagadoPorGrado(LocalDate inicioMes, LocalDate finMes);

    Page<Pago> buscarPagosPorNombreCompleto(String nombreCompleto, Pageable pageable);



}
