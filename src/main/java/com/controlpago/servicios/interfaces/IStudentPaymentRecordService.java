package com.controlpago.servicios.interfaces;

import com.controlpago.modelos.StudentPaymentRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IStudentPaymentRecordService {
    Page<StudentPaymentRecord>buscarTodosPaginados(Pageable pageable);
    List<StudentPaymentRecord>obtenerTodos();
    Optional<StudentPaymentRecord>buscarPorId(Integer id);
    StudentPaymentRecord crearOEditar(StudentPaymentRecord studentPaymentRecord);
    void eliminarPorId(Integer id);
}
