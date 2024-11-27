package com.controlpago.servicios.interfaces;

import com.controlpago.modelos.Pago;
import com.controlpago.modelos.StudentPaymentRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IStudentPaymentRecordService {
    Page<StudentPaymentRecord>buscarTodosPaginados(Pageable pageable);
    List<StudentPaymentRecord>obtenerTodos();
    Optional<StudentPaymentRecord>buscarPorId(Integer id);
    StudentPaymentRecord crearOEditar(StudentPaymentRecord studentPaymentRecord);
    void eliminarPorId(Integer id);
    Page<StudentPaymentRecord> buscarAlumnoPorNombreCompletoYFecha(String nombre, String apellido, LocalDate fecha, Pageable pageable);
}
